package lazy.test.tools.util;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;

import java.io.*;

import lazy.test.tools.util.model.CommonResult;
import lazy.test.tools.util.model.ErrorCode;
import lazy.test.tools.util.model.ScpMachineInfo;
/**
 * <b>工具说明：</b>提供远程执行linux shell脚本工具类， </br>
 * Date: 15-11-19 </br>
 * Time: 上午10:41  </br>
 */
public class ScpUtil {

	// can not instance a new object
	private ScpUtil() {
	}

	/**
	 * <b>工具说明：</b>:登陆远程linux machine并执行shell脚本 <br/>
	 * 
	 * @param info 远程linux主机的hostname、port、user、password;
	 * @param cmd  需要远程执行的shell命令行
	 * 
	 * @return 命令执行结果
	 */
	public static CommonResult<String> exeShellRemote(ScpMachineInfo info, String cmd) {

		CommonResult<String> cr = new CommonResult<String>();
		Connection c = getConnect(info);

		Session session = null;
		try {
			if (!c.authenticateWithPassword(info.getUser(), info.getPassword())) {
				cr.setSuccess(false);
				cr.setErrorMsg("请传入正确的用户名和密码！");
				cr.setErrorCode(ErrorCode.ERROR_USER_PW);
			}
			session = c.openSession();
			cmd = "sudo " + cmd;
			session.execCommand(cmd);

			for (int i = 0; i <= 20; i++) {
				if (session.getExitStatus() != 0) {
					Thread.sleep(500);
					continue;
				}
				break;
			}
			InputStream is = session.getStdout();
			String retStr = processInputStream(is, "UTF-8");
			
			cr.setT(retStr);
			cr.setSuccess(true);
			cr.setErrorCode(ErrorCode.SUCCESS);

		} catch (Exception e) {
			cr.setSuccess(false);
			cr.setErrorMsg("执行shell过程抛出异常！");
			cr.setErrorCode(ErrorCode.EXCEPTION);
		} finally {
			session.close();
			c.close();
		}
		return cr;
	}

	/**
	 * <b>工具说明：</b>:下载远程linux指定文件，并用字符串的方式返回<br/>
	 * <b>使用说明</b>：如果文件过大，可能造成内存溢出</br>
	 * 
	 * @param info 远程linux主机的hostname、port、user、password;
	 * @param remoteDir 远程目录
	 * @param remoteFile 远程文件名
	 * 
	 * @return 命令执行结果
	 */
	public static CommonResult<String> getRemoteFileContent(ScpMachineInfo info,
			String remoteDir, String remoteFile) {

		CommonResult<String> cr = new CommonResult<String>();
		Connection conn = getConnect(info);

		SCPClient scpClient = null;
		try {
			scpClient = conn.createSCPClient();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			OutputStream target = null;
			scpClient.get(remoteDir + remoteFile, target);
			String processStream = processOouputStream(target);
			cr.setT(processStream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return cr;
	}

	private static Connection getConnect(ScpMachineInfo info) {
		CommonResult<String> cr = new CommonResult<String>();
		if (info.getPort() < 1 || info.getPort() > 65535) {
			cr.setSuccess(false);
			cr.setErrorMsg("请传入正确的端口号！");
			cr.setErrorCode(ErrorCode.PORT_NEGATIVE);
			return null;
		}

		Connection conn = new Connection(info.getHostname(), info.getPort());
		try {
			conn.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}

	private static String processOouputStream(OutputStream os) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			os.write(baos.toByteArray());
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return baos.toString();
	}
	
	 private static String processInputStream(InputStream in, String charset) {  
	        byte[] buf = new byte[1024];  
	        StringBuilder sb = new StringBuilder();  
	        try {
				while (in.read(buf) != -1) {  
				    sb.append(new String(buf, charset));  
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}  
	        return sb.toString();  
	    }  

}
