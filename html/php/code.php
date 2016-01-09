<?php
    if(isset($_POST['code']))
	{
		$myfile = fopen("input.c", "w") or die("Unable to open file!");
		fwrite($myfile, $_POST['code']);
		fclose($myfile);
		
		system("python compiler.py");
		
		copy("code.asm","output/code.asm");
	
		system("CPU.exe");
		sleep(1);
		system("7z a -tzip coe.zip output/");
    }
?>