<?php
	
	$mycount = fopen("php/count.txt","r")or die("Unable to open file!");
	$count = fread($mycount,filesize("php/count.txt"));
	fclose($mycount);
	
	$mycount = fopen("php/count.txt","w")or die("Unable to open file!");
	fwrite($mycount,$count+1);
	fclose($mycount);
	
	$file_name = "coe".$count.".zip";     //�����ļ���  
	$file_dir = "./php/";        //�����ļ����Ŀ¼  
	
	//����ļ��Ƿ����  
	if (! file_exists ( $file_dir . $file_name )) {  
		echo "�ļ��Ҳ���";
		exit ();  
	} else {
		//���ļ�
		$file = fopen ( $file_dir . $file_name, "r" );  
		//�����ļ���ǩ   
		Header ( "Content-type: application/octet-stream" );  
		Header ( "Accept-Ranges: bytes" );  
		Header ( "Accept-Length: " . filesize ( $file_dir . $file_name ) );  
		Header ( "Content-Disposition: attachment; filename=" . $file_name );  
		//����ļ�����   
		//��ȡ�ļ����ݲ�ֱ������������  
		echo fread ( $file, filesize ( $file_dir . $file_name ) );  
		fclose ( $file );  
		exit ();  
	}  
?> 