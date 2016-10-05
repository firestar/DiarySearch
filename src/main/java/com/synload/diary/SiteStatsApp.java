package com.synload.diary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.UUID;

import com.synload.diary.partialSearch.Diary;
import com.synload.diary.partialSearch.WordChain;
import com.synload.eventsystem.events.annotations.Event;
import com.synload.framework.Log;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.annotations.Module;
import com.synload.talksystem.Client;
import com.synload.talksystem.ConnectionTypeDocument;
import com.synload.talksystem.info.InformationDocument;
import com.synload.talksystem.info.ServerTalkInformationEvent;
import org.apache.commons.io.IOUtils;

@Module(name="Site Statistics Plugin", author="Nathaniel Davidson", version="0.1", depend = {}, log= Module.LogLevel.INFO)
public class SiteStatsApp extends ModuleClass{
	public static Connection mainSQL;
	public static Diary index;
	public static Client connection;
    public static Client responder;
    public static Client searchStream;
	@Override
	public void initialize(){
		mainSQL = SynloadFramework.sql;
		try {
            Log.info("Initializing player thread", SiteStatsApp.class);
            SiteStatsApp.index = new Diary();
            String configFile = SynloadFramework.defaultPath+"diary.ini";
            Properties prop = new Properties();
            String address="";
            int port = 0;
            String key = "";
            if ((new File(configFile)).exists()) {
                prop.load(new FileInputStream(configFile));
                prop.getProperty("key")
            } else {
                InputStream is = SiteStatsApp.class.getClassLoader().getResourceAsStream("diary.ini");
                FileOutputStream os = new FileOutputStream(new File(configFile));
                IOUtils.copy(is, os);
                os.close();
                is.close();
                System.exit(0);
            }
			connection = Client.createConnection( address, port, false, key, true);
            responder = Client.createConnection( address, port, false, key, true);
            searchStream = Client.createConnection( address, port, false, key, true);
            searchStream.write(new InformationDocument("searchStream", UUID.randomUUID()));
            connection.write(new ConnectionTypeDocument());
			(new Thread(new IndexDiaryAdder())).start();
			Log.info("Done initializing", SiteStatsApp.class);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void crossTalk(Object... obj) {
		// TODO Auto-generated method stub
		
	}
	public static Stack<String> queue = new Stack<>();
	public static boolean trigger = true;
	public class IndexDiaryAdder implements  Runnable {
		@Override
		public void run() {
			while(trigger || queue.size()>0) {
                if(queue.size()>0) {
                    String[] o = queue.pop().split("&");
                    SiteStatsApp.index.addEntry(o[1], new Object[]{o[0]});
                    if (SiteStatsApp.index.getSize() % 1000 == 0) {
                        System.out.println("index diary size: " + SiteStatsApp.index.getSize());
                    }
                }else{
                    try {
                        Thread.sleep(1L);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
			}
		}
	}
    public static boolean rec = false;
	@Event(description = "get data from master", enabled = true, name = "IncomingData")
	public void incomingData(ServerTalkInformationEvent stm){
        if(stm.getiD().getType().equals("store")){
            if(!rec){
                System.out.println("received: "+(String)stm.getiD().getObjects().get("data"));
                rec=true;
            }
            queue.add((String)stm.getiD().getObjects().get("data"));
        }else if(stm.getiD().getType().equals("end")){
            trigger = false;
            System.out.println("END");
        }else if(stm.getiD().getType().equals("search")){
            System.out.println("looked up entry "+(String)stm.getiD().getObjects().get("term"));
            String ids = "";
            List<WordChain> wordChains = index.search((String)stm.getiD().getObjects().get("term"));
            for(WordChain wc: wordChains){
                if(!ids.equals("")){
                    ids+=", ";
                }
                if(wc !=null && wc.getData()!=null) {
                    ids += (String) wc.getData()[0];
                }
            }
            InformationDocument id = new InformationDocument("result", stm.getiD().getChain());
            id.getObjects().put("data", ids);
            try {
                responder.write(id);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            System.out.println("unknown "+stm.getiD().getType());
        }
	}

}