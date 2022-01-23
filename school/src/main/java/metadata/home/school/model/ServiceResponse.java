package metadata.home.school.model;

public class ServiceResponse {
    private Object result;

    private String message;

    public ServiceResponse(Object result, String message){
        this.result = result;
        this.message = message;
    }

    public Object getResult(){
        return result;
    }

    public String getMessage(){
        return message;
    }


}
