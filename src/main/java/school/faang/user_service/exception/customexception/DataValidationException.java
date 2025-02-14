package school.faang.user_service.exception.customexception;

public class DataValidationException extends RuntimeException{
    public DataValidationException(String message){
        super(message);
    }
}
