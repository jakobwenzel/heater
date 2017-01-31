package jakobwenzel.heater.model;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iotdata.AWSIotDataAsyncClient;
import com.amazonaws.services.iotdata.AWSIotDataClient;
import com.amazonaws.services.iotdata.model.PublishRequest;

import java.nio.ByteBuffer;

/**
 * Created by jwenzel on 18.01.17.
 */
public class Heater {
    private static Heater instance;

    private int setting;


    private final AWSIotDataClient client;
    private String endpoint;

    public Heater() {
        client = new AWSIotDataAsyncClient(new AWSCredentialsProviderChain(
                DefaultAWSCredentialsProviderChain.getInstance(),
                new ClasspathPropertiesFileCredentialsProvider())
        );
    }

    public synchronized static Heater getInstance() {
        if (instance==null)
            instance = new Heater();
        return instance;
    }


    public int getSetting() {
        return setting;
    }

    private int toAngle() {
        //1 needs to be 180
        //7 needs to be 0

        int reversed = 7-setting;
        // 1 is now 6
        // 7 is now 0

        return reversed*180/6;

    }
    public void setSetting(int setting) {
        if (setting <1)
            setting = 1;
        if (setting >7)
            setting = 7;
        this.setting = setting;

        try {
            PublishRequest publishRequest = new PublishRequest();
            publishRequest.setPayload(ByteBuffer.wrap(Integer.toString(toAngle()).getBytes()));
            publishRequest.setTopic("heater");
            client.publish(publishRequest);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void setRegion(String regionName) {
        client.setRegion(Region.getRegion(Regions.fromName(regionName)));
    }

    public void setEndpoint(String endpoint) {
        client.setEndpoint(endpoint);
    }
}
