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
        logger.debug("����������ΪUTF-8");
        p.put("file.encoding","UTF-8");
        System.setProperties(p);
        for (String s : args) {
            logger.debug("ɨ�赽������"+s);
            String arg = s
                    .replace("-", "")
                    .replace("=", "");
            if (arg.startsWith("cert")){
                cert = arg.replaceFirst("cert", "");
                logger.debug("��֤���ļ���Ϊ"+cert);
            }
            if (arg.startsWith("key")){
                key = arg.replaceFirst("key", "");
                logger.debug("����Կ�ļ���Ϊ"+key);
            }
        }
        logger.debug("����CenterNodeʵ��");
        CenterNode centerNode = new CenterNode(1109);
        logger.info("����LightCat���Ľڵ�");
        centerNode.start();
        logger.debug("�����ն�ʵ��");
        Terminal terminal;
        try {
            terminal = TerminalBuilder.builder()
                    .system(true)
                    .dumb(true)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.debug("����ն����[start,pause,stop]");
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
        logger.debug("�����ж�ȡ��ʵ��");
        LineReader lineReader = LineReaderBuilder.builder()
                .terminal(terminal)
                .completer(LightCatCompleter)
                .build();
        logger.debug("�����ն�ǰ׺Ϊ��>>��");
        String prompt = ">>";
        while (true) {
            try {
                String arg=lineReader.readLine(prompt);
                args = arg.split(" ");
                System.out.println("�û�ָ�"+arg);
                logger.debug("�û�ָ�"+arg);
                logger.info("�û�ָ�"+arg);
                logger.warn("�û�ָ�"+arg);
//                switch (args[0]){
//                    case "start ":{
//                        if(centerNode.isRunning())
//                            System.out.println("���Ľڵ���������");
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
        logger.info("ֹͣLightCat���Ľڵ�");
    }
}