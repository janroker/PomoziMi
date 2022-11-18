import http from "../http-common";
import jwt_decode from "jwt-decode";

const getCookie = (cname) => {
  var name = cname + "=";
  var decodedCookie = decodeURIComponent(document.cookie);
  //console.log(decodedCookie);
  var ca = decodedCookie.split(";");
  for (var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) === " ") {
      c = c.substring(1);
    }
    if (c.indexOf(name) === 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
};

class UserContext {
    constructor(id, roles) {
        this.id = id; // string id
        this.roles = roles; // string array 
    }
}

class UserService {
  getUserContext() {
    const token = getCookie("AUTH_COOKIE");
    if(token === undefined || token === null || token === "") return null;
    const decoded = jwt_decode(token);
    if(decoded === undefined || decoded === null) return null;
    return new UserContext(decoded.id, decoded.roles);
  }

  getUser(id) {
    return http.get("./users/" + id, {});
  }

  getUserStatistics(id) {
    return http.get("./users/statistics/" + id, {});
  }

  getAuthored(id) {
    return http.get("./requests/authored/" + id, {});
  }

  updateUser(id, user) {
    return http.put("./users/" + id, user);
  }

  getByExecutor(id) {
    return http.get("./requests/byExecutor/" + id, {});
  }

  blockUser(id, en) {
    return http.post("./users/blockUnblock/" + id +"?enabled=" + en, {});
  }

  blockRequest(id) {
    return http.patch("./requests/blockUnblock/" + id + "?enabled=false", {});
  }

  unblockRequest(id) {
    return http.patch("./requests/blockUnblock/" + id + "?enabled=true", {});
  }

  deleteRequest(id) {
    return http.delete("./requests/" + id, {});
  }

  updateRequest(id, updatedRequest) {
    return http.put("./requests/" + id, updatedRequest);
  }

  getChainOfTrust(id) {
    return http.get("./users/chainOfTrust/" + id, {});
  }
  
  getNotifications(id) {
    return http.get("/notifications/user/" + id , {});
  }

  setReadNotifs(id){
    return http.patch("/notifications/" + id, {});
  }

}

export default new UserService();
