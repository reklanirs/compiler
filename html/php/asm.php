<?php
    if(isset($_POST['code']))
	{
		$myfile = fopen("output/code.asm", "w") or die("Unable to open file!");
		fwrite($myfile, $_POST['code']);
		fclose($myfile);
		
		system("CPU.exe");
		sleep(1);
		system("7z a -tzip coe.zip output/");
    }
?>