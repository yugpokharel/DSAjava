import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

public class WebCrawler {
    private final ConcurrentLinkedQueue<String> urlQueue = new ConcurrentLinkedQueue<>();
    private final Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>());
    private final ExecutorService executorService;

    public WebCrawler(int maxThreads) {
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    public void startCrawling(String startUrl, int maxPages) {
        urlQueue.add(startUrl);

        while (!urlQueue.isEmpty() && visitedUrls.size() < maxPages) {
            String url = urlQueue.poll();
            if (url == null || visitedUrls.contains(url)) continue;

            visitedUrls.add(url);
            executorService.submit(new CrawlTask(url));
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nCrawling Finished! Total pages visited: " + visitedUrls.size());
    }

    private class CrawlTask implements Runnable {
        private final String url;

        public CrawlTask(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                System.out.println("Crawling: " + url);
                String content = fetchHTML(url);
                extractUrls(content);
            } catch (Exception e) {
                System.err.println("Failed to fetch: " + url);
            }
        }

        private String fetchHTML(String urlString) throws Exception {
            StringBuilder content = new StringBuilder();
            @SuppressWarnings("deprecation")
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString();
        }

        private void extractUrls(String html) {
            String regex = "href=[\"'](http[s]?://.*?)[\"']";
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            java.util.regex.Matcher matcher = pattern.matcher(html);

            while (matcher.find()) {
                String newUrl = matcher.group(1);
                if (!visitedUrls.contains(newUrl)) {
                    urlQueue.add(newUrl);
                }
            }
        }
    }

    public static void main(String[] args) {
        WebCrawler crawler = new WebCrawler(5); // Use 5 threads
        crawler.startCrawling("https://www.youtube.com/", 50); // Start crawling from example.com, max 50 pages
    }
}
