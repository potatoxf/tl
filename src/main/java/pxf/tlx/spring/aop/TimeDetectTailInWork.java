package pxf.tlx.spring.aop;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import pxf.tl.util.ToolBytecode;
import pxf.tl.util.ToolLog;
import pxf.tl.util.ToolTime;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author potatoxf
 */
@Getter
@Setter
public class TimeDetectTailInWork implements TailInWork {

    private int threshold = 3000;

    @Override
    public void execute(long executeTime, JoinPoint joinPoint, Object result, Throwable throwable) {
        if (joinPoint.getSignature() instanceof MethodSignature methodSignature) {
            Method method = methodSignature.getMethod();
            List<TimeDetect> timeDetects = ToolBytecode.lookupAnnotationOnElement(method, TimeDetect.class, false);
            if (!timeDetects.isEmpty()) {
                TimeDetect timeDetect = timeDetects.get(0);
                long thresholdTime;
                if (timeDetect != null) {
                    thresholdTime = timeDetect.threshold();
                } else {
                    thresholdTime = threshold;
                }
                int[] dateTimeField = ToolTime.getDateTimeField(executeTime);
                String timeFormat = "";
                if (dateTimeField[3] > 0) {
                    timeFormat += dateTimeField[3] + " Hour ";
                }
                if (dateTimeField[4] > 0) {
                    timeFormat += dateTimeField[4] + " min ";
                }
                if (dateTimeField[5] > 0) {
                    timeFormat += dateTimeField[5] + " s ";
                }
                if (dateTimeField[6] > 0) {
                    timeFormat += dateTimeField[6] + " ms";
                }
                ToolLog.debug(() -> "execute %s.%s time : %s", joinPoint.getTarget(), method.getName(), timeFormat);
                //执行事件超过设定的阈值，触发事件分发
                if (executeTime > thresholdTime) {
                    ToolLog.debug(() -> "execute method time too long!");
                }
            }
        }
    }
}
