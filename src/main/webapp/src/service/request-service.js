import http from "../http-common";

class RequestService {


    sendRequest(req){
      return http.post("/requests", req, {});
    }

    getRequests(radius){
      return http.get("/requests/active?radius=" + radius + "&page=0&size=400000", {});
    }

    getSearchRequests(radius, value){
      return http.get("/requests/active?radius=" + radius + "&page=0&size=400000&generalSearch=" + value, {});
    }

    sendExecution(id){
      return http.patch("/requests/pickForExecution/" + id, {});
    }

    deleteRequest(id){
        return http.delete("/requests/" + id, {});
    }

    confirmRequest(id) {
      return http.patch("/requests/confirmExecution/" + id + "?confirm=true", {});
    }

    blockRequest(id) {
      return http.patch("/requests/confirmExecution/" + id + "?confirm=false", {});
    }

    markExecuted(id) {
      return http.patch("/requests/markExecuted/" + id, {});
    }
  
  }
  
  export default new RequestService();