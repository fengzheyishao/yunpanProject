package com.yunpan.utils;

import com.yunpan.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessUtils {
    private static final Logger logger = LoggerFactory.getLogger(ProcessUtils.class);

    public static String executeCommand(String cmd, Boolean outprintLog) throws BusinessException {
        if (StringTools.isEmpty(cmd)) {
            logger.error("--- 指令执行失败，因为要执行的FFmpeg指令为空！ ---");
            return null;
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(cmd);

            PrintStream errorStream = new PrintStream(process.getErrorStream());
            PrintStream inputStream = new PrintStream(process.getInputStream());

            errorStream.start();
            inputStream.start();

            process.waitFor();

            String res = errorStream.sb.append(inputStream.sb + "/n").toString();

            if (outprintLog) {
                logger.info("执行命令:{}，已执行完毕,执行结果:{}", cmd, res);
            } else {
                logger.info("执行命令:{}，已执行完毕", cmd);
            }
            return res;

        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("视频转换失败");
        } finally {
            if (process != null) {
                ProcessKiller processKiller = new ProcessKiller(process);
                runtime.addShutdownHook(processKiller);
            }
        }
    }

    private static class ProcessKiller extends Thread {
        private Process process;
        public ProcessKiller(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            try {
                process.destroy();
            } catch (Exception e) {
                logger.error("ProcessKiller error", e);
            }
        }
    }

    private static class PrintStream extends Thread {
        InputStream is = null;
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();

        public PrintStream(InputStream is) {
            this.is = is;
        }

        @Override
        public void run() {
            try {
                if (is == null) {
                    return;
                }
                br = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ((line = br.readLine())!= null) {
                    sb.append(line);
                }
            } catch (Exception e) {
                logger.error("读取输入流出错了！错误信息：" + e.getMessage());
            } finally {
                try {
                    if (br!= null) {
                        br.close();
                    }
                    if (is!= null) {
                        is.close();
                    }
                } catch (Exception e) {
                    logger.error("调用PrintStream读取输出流后，关闭流时出错！");
                }
            }
        }
    }
}
