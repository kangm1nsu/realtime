package com.cos.realtime.batch;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cos.realtime.domain.NaverNews;
import com.cos.realtime.domain.NaverNewsRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

//동기적 배치프로그램
@RequiredArgsConstructor
@Component
public class naverCrawBatch {
	
	private final NaverNewsRepository naverNewsRepository;
	
	// Long 8 byte
		long aid = 277493;

		// 2021.10.12
		// 2021.10.20
		
//		@Scheduled(cron = "**1***", zone = "Asia/Seoul")
		@Scheduled(cron = "0 40 12 * * *", zone = "Asia/Seoul")
		public void collectTest() {
			List<NaverNews> naverNewsList = new ArrayList<>();
			int errorCount = 0;
			int successCount = 0;
			while (true) {
				
				String aidStr = String.format("%010d", aid);
				String url = "https://news.naver.com/main/read.naver?mode=LSD&mid=shm&sid1=103&oid=437&aid=" + aidStr;

				try {
					Document doc = Jsoup.connect(url).get();
					System.out.println("aidstr : " + aidStr);
					String title = doc.selectFirst("#articleTitle").text();

					String company = doc.selectFirst(".press_logo img").attr("alt");
					String createdAt = doc.selectFirst(".t11").text();

					System.out.println(title);
					System.out.println(company);
					System.out.println(createdAt);
					
					LocalDate today = LocalDate.now();
					LocalDate yesterday = today.minusDays(1);
					System.out.println(yesterday);

					createdAt = createdAt.substring(0, 10);
					createdAt = createdAt.replace(".", "-");
					System.out.println(createdAt);
					
					if(today.toString().equals(createdAt)) {
						System.out.println("createdAt :" + createdAt);
						break;
					}
					
					if (yesterday.toString().equals(createdAt)) {
						naverNewsList.add(NaverNews.builder()
								.title(title)
								.company(company)
								.createdAt(Timestamp.valueOf(LocalDateTime.now().minusDays(1).plusHours(9)))
								.build()
								);
						
						successCount++;
					}

				} catch (Exception e) {
					System.out.println(e.getMessage());
					errorCount++;
				} 
					aid++;
				
				
			}//end of while
			System.out.println("배치프로그램 종료");
			System.out.println("성공횟수" + successCount);
			System.out.println("실패횟수" + errorCount);
			System.out.println("마지막 aid값" + aid);
			System.out.println("컬렉션에 담은크기"+ naverNewsList.size());
			
			Flux.fromIterable(naverNewsList)
				.flatMap(naverNewsRepository::save)
				.subscribe();
				
		}
}
