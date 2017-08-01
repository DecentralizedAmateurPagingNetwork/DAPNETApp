package de.hampager.dapnetmobile.api.error;

import java.io.IOException;
import java.lang.annotation.Annotation;

import de.hampager.dapnetmobile.api.ServiceGenerator;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {
    private ErrorUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static APIError parseError(Response<?> response) {
        Converter<ResponseBody, APIError> converter =
                ServiceGenerator.retrofit
                        .responseBodyConverter(APIError.class, new Annotation[0]);

        APIError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new APIError();
        }

        return error;
    }
}
