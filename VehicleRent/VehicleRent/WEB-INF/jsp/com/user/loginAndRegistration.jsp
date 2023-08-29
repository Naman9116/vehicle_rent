<html>
<head>
<title>EPM</title>
<style type="text/css">

	.header{
		text-align: center;
		line-height:10px; ;
		border-radius: 10px;	
	    border: solid green 2px;
		box-shadow: 0px 0px 10px green;
	}

	.header h1{
		font-size: 55px;
		text-shadow: 0px 0px 2px green;
	}

	.login{
		border-radius: 10px;
	    border: solid green 2px;
		box-shadow: 0px 0px 10px green;
		font-family: serif;
	}

</style>
</head>
<body style='background-color: #E5E4E2;'><BR><BR><BR><BR>
		
		<TABLE  border=0 width='80%' height='80%' align='center'>
		<TR>
			<TD colspan=2>
				<TABLE class="header" width='100%' >
				<TR>
					<TD><H3>Vehicle Management System</H3></TD>
				</TR>
				</TABLE>	
			</TD>
		</TR>
		<TR>
			<TD>
				<TABLE  class="login" width='100%' height='100%' align='center'>
				<TR>
				<TD>
					<TABLE align='center' >
					<FORM METHOD=POST ACTION="userLogin.html" NAME='userLoginForm'>			
						<TR align='center'>
							<TD colspan=2><H3>Login Page</H3></TD>
						</TR>
						<TR>
							<TD>User Name</TD>
							<TD><INPUT TYPE="text" NAME="username"></TD>
						</TR>
						<TR>
							<TD>Password</TD>
							<TD><INPUT TYPE="password" NAME="password"></TD>
						</TR>
						<TR>
							<TD></TD>
							<TD><INPUT TYPE="submit" value="Login"> </TD>
						</TR>
					</FORM>
					</TABLE>
				</TD>
				</TR>
				</TABLE>   	
			</TD>
			<TD>
				<TABLE  class="login" width='100%' height='100%' align='center'>
				<TR>
				<TD>
					<TABLE align='center' >
					<FORM METHOD=POST ACTION="userRegistration.html" NAME='userRegistrationForm' >			
						<TR align='center'>
							<TD colspan=2><H3>Registration Page</H3></TD>
						</TR>
						<TR>
							<TD>Name</TD>
							<TD><INPUT TYPE="text" NAME="regUsername"></TD>
						</TR>
						<TR>
							<TD>Password</TD>
							<TD><INPUT TYPE="password" NAME="regPassword"></TD>
						</TR>
						<TR>
							<TD>Re-Password</TD>
							<TD><INPUT TYPE="password" NAME="regRePassword"></TD>
						</TR>
						<TR>
							<TD>Mobile</TD>
							<TD><INPUT TYPE="text" NAME="regMobile"></TD>
						</TR>
							<TR>
							<TD></TD>
							<TD> <INPUT TYPE="submit" value="SignUp"></TD>
						</TR>
					</FORM>
					</TABLE>
				</TD>
				</TR>
				</TABLE> 
			</TD>
		</TR>
		</TABLE>
</body>
</html>
