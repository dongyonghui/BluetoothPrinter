package cn.vip.dw.bluetoothprinterlib.velocity;

import org.apache.commons.lang.StringUtils;

/**
 * 格式化小票上的第一列文字,通常为商品或套餐名
 * 
 * 类名: FirstColumnTool <br/>
 * 日期: 2015年12月8日 下午4:52:30 <br/>
 * 
 * @author shilei
 */
public class FirstColumnTool {
	/**
	 * 格式化 format:(方法功能描述). <br/>
	 * 
	 * (方法的适用场景及注意事项).<br/>
	 * 
	 * 日期: 2015年12月8日 下午4:53:06 <br/>
	 * 
	 * @param content
	 *            显示内容
	 * @param eol
	 *            换行符
	 * @param lineSize
	 *            每行长度
	 * @param fieldSize
	 *            列长度
	 * @return
	 * @author shilei
	 */
	public String format(String content, String eol, int lineSize, int fieldSize) {
		StringBuilder sb = new StringBuilder();
		int charCount = 0;
		int charCountLastLine = 0;
		char[] chars = content.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] >= 33 && chars[i] <= 126) {
				charCount++;
				charCountLastLine++;
			} else {
				charCount += 2;
				charCountLastLine += 2;
			}

			sb.append(chars[i]);
			if (charCount % lineSize == 0) {
				sb.append(eol);
				charCountLastLine = 0;
			}
		}
		// 当最后一行长度超过每列应占长度时,换行
		if (charCountLastLine > fieldSize) {
			sb.append(eol);
			charCountLastLine = 0;
		}
		// 补空格
		String pad = StringUtils.rightPad("", fieldSize - charCountLastLine, ' ');
		return sb.append(pad).toString();
	}
}
