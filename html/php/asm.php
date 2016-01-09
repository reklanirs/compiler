<?php
    if(isset($_POST['code']))
	{
		$myfile = fopen("code.asm", "w") or die("Unable to open file!");
		fwrite($myfile, $_POST['code']);
		fclose($myfile);
		sleep(1);
		system("CPU.exe");
		sleep(1)
		copy("code.asm","output/code.asm");
		
		$mycount = fopen("count.txt","r")or die("Unable to open file!");
		$count = fread($mycount,filesize("count.txt"));
		fclose($mycount);
		system("7z a -tzip coe".$count.".zip output/");
		exit();
    }
?>