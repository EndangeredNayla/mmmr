@FOR /F "tokens=2* delims=	 " %%A IN ('REG QUERY "HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Runtime Environment" /v CurrentVersion') DO @SET CurrentVersion=%%B
@FOR /F "tokens=2* delims=	 " %%A IN ('REG QUERY "HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Runtime Environment\%CurrentVersion%" /v JavaHome') DO @SET JavaHome=%%B
"%JavaHome%\bin\java.exe" -jar mmmr*.bat -console=true
@pause