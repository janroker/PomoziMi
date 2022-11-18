import http from "../http-common";

class CandidacyService {

    getCandidacies(year) {
        if(year === undefined) return http.get("./candidacies");
        return http.get("./candidacies?year=" + year);
    }
    
    candidateYourself() {
        return http.post("./candidacies");
    }

    deleteCandidacy() {
        return http.delete("./candidacies");
    }
  
  }
  
  export default new CandidacyService();