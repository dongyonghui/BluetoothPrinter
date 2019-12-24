package cn.vip.dw.bluetoothprinterlib.velocity;

import java.util.Map;

/**
 * 
 * 类名: ContentRender <br/>
 * 日期: 2015年12月8日 下午12:20:10 <br/>
 * 
 * @author shilei
 */
public interface ContentRender {

	/**
	 * 生成打印内容.
	 * 
	 * render:(方法功能描述). <br/>
	 * 
	 * (方法的适用场景及注意事项).<br/>
	 * 
	 * 日期: 2015年12月8日 下午12:20:01 <br/>
	 * 
	 * @param template
	 *            模板
	 * @param values
	 *            填充值
	 * @return
	 * @author shilei
	 */
	String render(String template, Map<String, Object> values);
}
