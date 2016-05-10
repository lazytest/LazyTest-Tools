/**
 * 
 */
package lazy.test.tools.util.model;

/** 
 * ClassName: CommonResult <br/> 
 * Function: 通用的函数返回值. <br/> 
 * 
 * @version @param <T> 
 * @since JDK 1.7
 */ 
public class CommonResult<T> extends BaseResult {
	
	/**
	 * 泛型，函数返回信息
	 */
	private T target;

	/**
	 * @return the t
	 */
	public T getT() {
		return target;
	}

	/**
	 * @param target the t to set
	 */
	public void setT(T target) {
		this.target = target;
	}

}
