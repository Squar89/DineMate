package FoodRecipeGenerator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

class RestAPICaller
{
    static String call(String url)
    {
        String result = "error";
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build())
        {
            HttpGet httpGetRequest = new HttpGet(url);

            // Execute HTTP request
            HttpResponse httpResponse = httpClient.execute(httpGetRequest);

            // Print the HTTP response
            // System.out.println(httpResponse.getStatusLine());

            // Get hold of the response entity
            HttpEntity entity = httpResponse.getEntity();

            // If the response does not enclose an entity, there is no need
            // to bother about connection release
            byte[] buffer = new byte[1000 * 1000 * 1000];
            if (entity != null)
            {
                InputStream inputStream = entity.getContent();
                try
                {
                    int bytesRead = 0;
                    BufferedInputStream bis = new BufferedInputStream(inputStream);
                    while ((bytesRead = bis.read(buffer)) != -1)
                    {
                        result = new String(buffer, 0, bytesRead);
                    }
                }
                catch (IOException ioException)
                {
                    // In case of an IOException the connection will be released
                    // back to the connection manager automatically
                    ioException.printStackTrace();
                }
                catch (RuntimeException runtimeException)
                {
                    // In case of an unexpected exception you may want to abort
                    // the HTTP request in order to shut down the underlying
                    // connection immediately.
                    httpGetRequest.abort();
                    runtimeException.printStackTrace();
                }
                finally
                {
                    // Closing the input stream will trigger connection release
                    try
                    {
                        inputStream.close();
                    }
                    catch (Exception ignore)
                    {
                    }
                }
            }
        }
        catch (ClientProtocolException e)
        {
            // thrown by httpClient.execute(httpGetRequest)
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // thrown by entity.getContent();
            e.printStackTrace();
        }
        return result;
    }
}
