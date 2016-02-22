package uk.ac.le.cs.CO3090.cw1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WebBot implements Runnable, StatisticalAnalysisInterface {

	public static int MAX_PAGES_NUM = 50;
	public static int TIME_OUT = 10000;
	public static int MAX_QUEUE_SIZE = 20000;
	public static int MAX_THREAD_NUM = 10;
	public static int MAX_CHAR_COUNT = 1000000;
	public static String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

	int current_character_count = 0;
	static int total_character_count = 0;

	private static List<String> URLs = Collections.synchronizedList(new ArrayList<>());
	private Map<Character, Integer> letterOccurrencePage;
	private static Map<Character, Integer> letterOccurrenceOverall = Collections.synchronizedMap(new HashMap<>());
	private String URL;

	@Override
	public void run() {
		synchronized (URLs) {
			//System.out.println(Thread.activeCount());
			if ((URLs.size() > MAX_PAGES_NUM || total_character_count > MAX_CHAR_COUNT)) {
				return;
			}
		}
		// System.out.println(this.URL);
		try {
			this.count(this.URL);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void count(String URL) throws InterruptedException {

		synchronized (URLs) {
			if (URLs.contains(URL)) {
				// System.out.println("This page has been visited: " + URL);
				return;
			}
			URLs.add(URL);
		}
		//System.out.println("Visiting: " + URL);
		String websiteURL = URL;

		// get HTML
		String content = WebExtractor.getTextFromAddress(websiteURL);

		// strips HTML tags
		String text = WebExtractor.getPlainText(content);

		this.letterOccurrencePage = WebExtractor.calculate(text);
		for (Character c : WebBot.ALPHABET.toCharArray()) {
			synchronized (WebBot.letterOccurrenceOverall) {
				Integer currentCount = WebBot.letterOccurrenceOverall.get(c);
				if (currentCount == null)
					currentCount = 0;
				currentCount += this.letterOccurrencePage.get(c);
				WebBot.letterOccurrenceOverall.put(c, currentCount);
				this.current_character_count += currentCount;
				total_character_count += currentCount;
			}
		}

		ArrayList<String> urls = WebExtractor.extractHyperlinks(URL, WebExtractor.getTextFromAddress(URL));
		for (String url : urls) {
			if (Thread.activeCount() < MAX_THREAD_NUM) {
				(new Thread(new WebBot(url))).start();
			}
		}
	}

	@Override
	public void showTotalStatistics() {
		System.out.println("Total number of characters: " + total_character_count);
		System.out.println("Pages visited: " + URLs.size());
		Iterator it = letterOccurrenceOverall.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        Character letter = (Character) pair.getKey();
	        Integer count = (Integer)pair.getValue();
	        Double percentage = (count.doubleValue()*100)/Integer.valueOf(total_character_count).doubleValue();
	        System.out.println(String.format("%c = %.3f%%", letter, percentage.doubleValue()));
	        //System.out.println(letter + " = " + percentage + "%");
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}

	public WebBot(String URL) {
		this.URL = URL;
		this.letterOccurrencePage = Collections.synchronizedMap(new HashMap<>());
	}

	public static void main(String[] args) {

		String websiteURL = "http://www.theguardian.com/uk";
		Thread t = new Thread(new WebBot(websiteURL));
		t.start();
		while(Thread.activeCount() > 1){
		}
		if ((URLs.size() > MAX_PAGES_NUM || total_character_count > MAX_CHAR_COUNT) || Thread.activeCount() == 1) {
			WebBot stats = new WebBot(null);
			stats.showTotalStatistics();
			return;
		}

	}

}
