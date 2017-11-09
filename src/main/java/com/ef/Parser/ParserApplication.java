package com.ef.Parser;

import com.ef.Parser.com.ef.repository.AccessLog;
import com.ef.Parser.com.ef.repository.AccessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

@SpringBootApplication
public class ParserApplication implements CommandLineRunner {

	@Autowired
	DataSource dataSource;

	@Autowired
	AccessLogRepository accessLogRepository;

	public static void main(String[] args) {
		SpringApplication.run(ParserApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		Map<String, String> params = parseParam(args);


		if (params.containsKey("accesslog")) {
			// load access log to table accesslog
			String file = params.get("accesslog") ;
			loadLogToDatabase(file);
		}else {
			// Find IP with condition and insert to table blackip
			String startDate = params.get("startDate");
			int threadhold = Integer.parseInt(params.get("threshold"));
			String duration = params.get("duration");
			findBlockIpAndInsertDatabase(startDate, duration, threadhold);
		}

	}

	public void findBlockIpAndInsertDatabase(String startDate, String duration, int threshold) {
		Map<String, Integer> blockIpMap ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd'.'HH:mm:ss") ;
		try {
			blockIpMap = accessLogRepository.findBlockIP(dateFormat.parse(startDate), duration, threshold);
			blockIpMap.entrySet().stream().forEach(ip -> {
						String blockIp = ip.getKey() ;
						System.out.println(blockIp);
						String comment  =  "Number Request =  " + ip.getValue()  + " , fromdate = " + startDate + " ,  threshold = " + threshold + ", duration = " + duration;
						accessLogRepository.insertBlockIp(blockIp, comment);
					}) ;

		}catch (ParseException pex) {
			pex.printStackTrace();
		}

	}

	public void loadLogToDatabase(String file) {
		try {
			List<AccessLog> accessLogs = new ArrayList<>();
			Stream<String> stream = Files.lines(Paths.get(file));
			stream.forEach(line -> {
				String data[] = line.split("\\|");
				try {
					accessLogs.add(new AccessLog(data[0], data[1], data[2], Integer.parseInt(data[3]), data[4]));
				}catch (Exception pex) {
					pex.printStackTrace();
				}
			});
			accessLogRepository.insertBatch(accessLogs);


		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	public Map parseParam(String args[]) {
		Map<String, String> result = new HashMap<>();
		if (args.length <= 0) {
			System.err.println("[Usage] java xxx.jar [--accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100]");
		} else{
			System.out.println("Parameter length : " + args.length);
			for (int i = 0 ; i < args.length; i++) {
				result.put(args[i].split("=")[0].replace("--",""), args[i].split("=")[1]);
			}
			System.out.println("Success parse arguments : " + result);
		}


		return result;
	}
}
