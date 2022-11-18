import http from "../http-common";

class RatingService {
  rateUser(id, rating) {
    return http.post("./ratings/" + id, rating);
  }

  rateRequest(idUser, idReq, rating) {
    return http.post("./ratings/" + idUser + "?idReq=" + idReq, rating);
  }
}

export default new RatingService();
