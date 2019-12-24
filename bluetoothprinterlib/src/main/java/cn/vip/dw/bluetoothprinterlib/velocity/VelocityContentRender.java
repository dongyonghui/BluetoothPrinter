package cn.vip.dw.bluetoothprinterlib.velocity;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Map;

/**
 * 使用Velocity模板生成内容
 * <p/>
 * 类名: VelocityContentRender <br/>
 * 日期: 2015年12月8日 下午12:48:25 <br/>
 *
 * @author shilei
 */
public class VelocityContentRender implements ContentRender {

    /**
     * 构造方法 VelocityContentRender.
     */
    public VelocityContentRender() {
        Velocity.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogChute");
        Velocity.init();
    }

    /**
     * 生成内容
     */
    @Override
    public String render(String templateInfoString, Map<String, Object> values) {
        VelocityContext context = new VelocityContext(values);
        StringWriter writer = new StringWriter();
        try{
            VelocityEngine ve = new VelocityEngine();
            ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogChute");
            ve.init();
            // 转换输出
            ve.evaluate(context, writer, "", templateInfoString); // 关键方法
        }catch (Exception e){
            e.printStackTrace();
        }
        return writer.toString();
    }
}
