import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

// парсим новости от Коммерсантъ https://www.kommersant.ru
public class Main {
    public static void main (String[] args) {

            try {
                // 1. Подключение
                System.out.println("Запрос к сайту...");
                String url = "https://www.kommersant.ru";
                Document doc = Jsoup.connect(url).get();
                // 2. Сначала смотрим в меню сайта, какие рубрики нам доступны (получаем содержимое списков меню)
                Element mainmenu = doc.getElementById("site_menu");
                List<Element> liitems = mainmenu.getElementsByAttributeValue("class", "site_menu__item");
                if( liitems.size()==0) throw new Exception();
                StringBuffer rubrics = new StringBuffer();
                for( int i=0; i<liitems.size(); i++ ) {
                    rubrics.append(liitems.get(i).getElementsByTag("a").html()+" | ");
                    if( (i+1)%5 == 0) rubrics.append("\n");
                }
                // 3. Теперь даём пользователю возможность выбрать, список статей какой рубрики просмотреть
                // или выйти
                String rubricUrl;
                String rubric;
                while(true){
                    System.out.println("\nВведите название рубрики или exit для выхода\n");
                    System.out.println(rubrics);
                    Scanner scanner = new Scanner(System.in);
                    rubric = scanner.nextLine();
                    if( rubric.equals("exit"))
                        break;
                    rubricUrl = null;
                    for (Element liitem : liitems)
                        if (liitem.getElementsByTag("a").html().equals(rubric)) {
                            String href = liitem.getElementsByTag("a").get(0).attr("href");
                            rubricUrl = href.startsWith("//") ? "https:"+href : url+href;
                            break;
                        }
                    // 4. Парсим выбранную рубрику и возвращаем список всех статей на странице (они показаны за 1 день)
                    System.out.println(rubricUrl);
                    parseRubric(rubricUrl);
                }
            } catch (NullPointerException e) {
                System.out.println("Не получается найти меню сайта");
            } catch (IOException e) {
                System.out.println("Проблемы с соединением");
            } catch (Exception e) {
                System.out.println("Не получается обработать меню сайта");
            }
    }
    static void parseRubric(String url) {
        if( url==null )
            System.out.println("Проверьте название выбранной рубрики");
        else{
            try {
                //String url = "https://www.kommersant.ru/rubric/4?from=burger";
                Document doc = Jsoup.connect(url).get();
                Element mainpart = doc.getElementsByTag("main").get(0);
                List<Element> articles = mainpart.getElementsByTag("article");
                if( !articles.get(0).hasText() || articles.get(0).attr("data-article-title").isEmpty())
                    throw new Exception();
                for (Element article : articles) {
                    System.out.println(article.attr("data-article-title") + " - "
                            + article.attr("data-article-url"));
                    if (!article.attr("data-article-description").isEmpty())
                        System.out.println("(" + article.attr("data-article-description") + ")");
                }
            } catch (Exception e) {
                System.out.println("Не получается обработать страницу с указанной рубрикой.\n Просмотрите станицу в браузере "+ url);
            }
        }
    }
}
