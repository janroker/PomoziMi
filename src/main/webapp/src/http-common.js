import axios from "axios";
import UserService from "./service/user-service"
const { REACT_APP_BASE_URL } = process.env;

const client = axios.create({
  withCredentials: true,
  xsrfCookieName: 'X-CSRF-COOKIE', // default,
  xsrfHeaderName: 'X-CSRF-TOKEN',
  baseURL: REACT_APP_BASE_URL,
  headers: {
    "Content-type": "application/json"
  }
});

client.interceptors.response.use(
  res => res,
  err => {
    if (err.response.status === 403 && err.response.data.code === 1001) {
      let userId = UserService.getUserContext().id;
      if (userId !== undefined) {
        alert("Morate na profilu ocijeniti sve izvršene zahtjeve prije vaše iduće akcije");
        window.location.assign("/profile/" + userId);
      } else window.location.assign("/home");
    }
    throw err;
  }
)

export default client