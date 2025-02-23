package school.faang.user_service.exception.customexception;

public class CSVFileException extends RuntimeException{

    public CSVFileException(String message, Throwable cause){
        super(message, cause);
    }

    public CSVFileException(String message){
        super(message);
    }
}
