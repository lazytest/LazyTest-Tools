/**
 * 
 */
package lazy.test.tools.util.model;

/** 
 * ClassName: BaseResult <br/> 
 * Function: 函数返回值基本类型. <br/> 
 * 
 * @version  
 * @since JDK 1.7
 */ 
public class BaseResult {
	
	/**
	 * 是否成功
	 */
	private boolean isSuccess;
	
	/**
	 * 错误码
	 */
	private ErrorCode errorCode;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;

	/**
	 * @return the isSuccess
	 */
	public boolean isSuccess() {
		return isSuccess;
	}

	/**
	 * @param isSuccess the isSuccess to set
	 */
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	/**
	 * @return the errorCode
	 */
	public ErrorCode getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
