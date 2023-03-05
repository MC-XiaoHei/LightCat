package xor7studio.lightcat;

import org.jline.reader.*;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.NullCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class Main{
    private static final Logger logger = LoggerFactory.getLogger("LightCat");
    public static String cert="cert",key="key";
    public static void main(String[] args) {
        Properties p = System.getProperties();
        logger.debug("将编码设置为UTF-8");
        p.put("file.encoding","UTF-8");
        System.setProperties(p);
        for (String s : args) {
            logger.debug("扫描到参数："+s);
            String arg = s
                    .replace("-", "")
                    .replace("=", "");
            if (arg.startsWith("cert")){
                cert = arg.replaceFirst("cert", "");
                logger.debug("将证书文件设为"+cert);
            }
            if (arg.startsWith("key")){
                key = arg.replaceFirst("key", "");
                logger.debug("将密钥文件设为"+key);
            }
        }
        logger.debug("创建CenterNode实例");
        CenterNode centerNode = new CenterNode(1109);
        logger.info("启动LightCat中心节点");
        centerNode.start();
        logger.debug("创建终端实例");
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder()
                    .system(true)
                    .dumb(true)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.debug("添加终端命令：[start,pause,stop]");
        Completer startCompleter = new ArgumentCompleter(
                new StringsCompleter("start"),
                NullCompleter.INSTANCE
        );
        Completer pauseCompleter = new ArgumentCompleter(
                new StringsCompleter("pause"),
                NullCompleter.INSTANCE
        );
        Completer stopCompleter = new ArgumentCompleter(
                new StringsCompleter("stop"),
                NullCompleter.INSTANCE
        );
        Completer LightCatCompleter = new AggregateCompleter(
                startCompleter,
                pauseCompleter,
                stopCompleter
        );
        logger.debug("创建行读取器实例");
        LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(LightCatCompleter)
                .build();
        logger.debug("设置终端前缀为“>>”");
        String prompt = ">>";
        while (true) {
            try {
                String arg=lineReader.readLine(prompt);
                args = arg.split(" ");
                System.out.println("用户指令："+arg);
                logger.debug("用户指令："+arg);
                logger.info("用户指令："+arg);
                logger.warn("用户指令："+arg);
//                switch (args[0]){
//                    case "start ":{
//                        if(centerNode.isRunning())
//                            System.out.println("中心节点已在运行");
//                        else centerNode.start();
//                    }
//                }
            } catch (UserInterruptException e) {
                System.out.println("\n");
            } catch (EndOfFileException e) {
                stop();
                return;
            }
        }
    }
    public static void stop(){
        logger.info("停止LightCat中心节点");
    }
}