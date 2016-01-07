<?php  
    if(isset($_POST['code']))
	{
        echo '1'; 
		$myfile = fopen("code.c", "w") or die("Unable to open file!");
		fwrite($myfile, $_POST['code']);
		fclose($myfile);
    }
?>