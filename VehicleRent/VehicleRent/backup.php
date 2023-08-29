<?php
function LetsTakeBackup(){
    	$backup_directory = '/opt/backup/';
	
	/*Delete all files before backup*/
    	$cmd = 'rm '.$backup_directory .'*';
	exec($cmd);
	
	/*Backup the mysql survey DB*/
    	$filenameOrg = time() . '_' . 'vrms_backup.sql.gz';
    	$cmd = 'mysqldump -u root -pntplupl vehiclerent | gzip > ' . $backup_directory . $filenameOrg;
    	exec($cmd);

	/*Download the backup to machine*/
	$filename=''.$backup_directory.$filenameOrg;
	@header("Content-type: application/zip");
	@header("Content-Disposition: attachment; filename=$filenameOrg");
	echo file_get_contents($filename);
}
LetsTakeBackup();
?>