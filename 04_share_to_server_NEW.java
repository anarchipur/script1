/*

date: 18.12.2017

Module: 
Attach

Distribution:
share from: 
 dev  (share) -> dev  Server
 dev  (share) -> qual Server
 qual (share) -> prod Server
 
 [qual (share) -> qual Server]
 
 dev  (share) -> qual (share)
 qual (share) -> prod (share)
Domain:

server


*/

pipeline {
	//the label of the slave node to be used for build jobs
	agent any
	/*TODO entkommentieren
	agent {
		label "amu123-jnode-prod"
    }
	*/
    
    stages {		
		stage("Input Check") {
			steps {	
				script {	
					print "Starting script...!"
					if (Module_UserChoice == "Select module..." || Module_UserChoice == "") {
						print " ========> No module/component selected - EXITING!"
						currentBuild.result = 'FAILURE'
						error("========> No module selected!")
						return
					}	
					
					if (Distribution_UserChoice == "Select distribution..." || Distribution_UserChoice == "") {
						print " ========> No distribution selected - EXITING!"
						currentBuild.result = 'FAILURE'
						error(" ========> No distribution selected!")
						return
					}
					
					if (ServerDomain_UserChoice == "Select server domain..." || ServerDomain_UserChoice == "") {
						print " ========> No distribution selected - EXITING!"
						currentBuild.result = 'FAILURE'
						error(" ========> No distribution selected!")
						return
					}	
					
				}
			}						
		}
		
		stage("Wintraui-Copy") {
			steps {								
				
				timeout(time: 3, unit: 'MINUTES') {
					script {
						//archiv directory for the module wihtin the module directory on the destination						
						String ArchivDir = "__#lastversion"
						String DestEnvironment = "UNDEFINED"
						String srcPath = "UNDEFINED"
						String destPath = "UNDEFINED"
						
						String command1 = "UNDEFINED"
						int robocopy_result = -999;
						
						Obj o = new Obj()
						o.myPrint()
						
						print "Starting Wintraui...!"
						//check the user selection
						if (Distribution_UserChoice== "dev  (share) -> dev  Server") {
							source_share = "dev"
							dest_share = "dev"
							dest_server = "dev"
						} else {
							DestEnvironment = "prod"
						}
						
						
						//source directory
						srcPath = "\\\\wintraui-prod\\wintraui_dist\\${source_share}\\PLM\\${Module_UserChoice}"
						//destination directory
						destPath = "\\\\wintraui-prod\\wintraui_dist\\${dest_share}\\PLM\\${Module_UserChoice}"
						
						print "EXECUTE BATCH: create archive dir if not exists..."
						//bat(script: "if not exist ${destPath}\\${ArchivDir} mkdir ${destPath}\\${ArchivDir}", returnStatus: false)
						
						print "EXECUTE BATCH: clean up archive directory on destination ..."
						//bat(script: "del /S ${destPath}\\${ArchivDir} /Q", returnStatus: false)
						
						print "EXECUTE BATCH: move last version to archive dir on destination..."
						//robocopy_result = bat(script: "C:\\Windows\\System32\\robocopy.exe ${destPath} ${destPath}\\${ArchivDir} /MIR /IS /IT /XD __#* /R:2 /W:10", returnStatus: true)
						print " ========>  robocopy-result=" + robocopy_result
						if (robocopy_result > 3) {
							print " ========> ERROR: Archiv copy failed; robocopy-result=" + robocopy_result + " - EXITING!"
							currentBuild.result = 'FAILURE'
						}
						
						print "EXECUTE BATCH: delete all files in destination main directory but NOT subdirectories and their files..."
						bat(script: "del ${destPath} /Q", returnStatus: false)
						
						print "EXECUTE BATCH: move files from source to destination..."
						//robocopy_result = bat(script: "C:\\Windows\\System32\\robocopy.exe ${srcPath} ${destPath} /E /IS /IT /XD __#* /R:2 /W:10", returnStatus: true)
						print " ========>  robocopy-result=" + robocopy_result
						if (robocopy_result > 3) {
							print " ========> ERROR: Copy to destination failed; robocopy-result=" + robocopy_result + " - EXITING!"
							currentBuild.result = 'FAILURE'
						}						
					}
				}			
			}						
		}
    }
	
	/*TODO entkommentieren
	post {
		failure {
			emailext (
				to: 'sergey.georgiev@rohde-schwarz.com',
				subject: "Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
				body: """<html><head></head><body><p style="background-color:#FF0000">FAILURE: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
				<p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p></body></html>""",
				mimeType: "text/html",
				attachLog: true,
			)
		}
	}
	*/
}

class Obj {
	
	private destination = "NO DEST"
	
	Obj(dest){
		this.destination = dest
	}
	
	def myPrint(){
		print "Hello !!!!!!!!!!!!!!!!! " + this.destination
	}
}
