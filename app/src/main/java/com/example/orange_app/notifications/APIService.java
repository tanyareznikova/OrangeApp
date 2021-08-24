package com.example.orange_app.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA5oOfG8U:APA91bHVcZznW0-dEXoLYu7inN3vj1Oi6gvIy61wByilujLQ3b3tHUEKOcKnYfJkhOeDPVATkSFH0fkFVl0U0fgCIhEDzgkznIdyxfQbsVUGWdcQJHqtknSHLoBcoh3IknCztfDYrkVD"
    })
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);

}//APIService
