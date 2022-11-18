import React, { lazy, Suspense } from 'react';
import './App.modules.css';
import { BrowserRouter, Switch, Route } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import UserService from "./service/user-service";
import LoginService from "./service/login-service";

const Login = lazy(() => import("./components/login"));
const Home = lazy(() => import("./components/home"));
const Registration = lazy(() => import("./components/registration"));
const RequestLoader = lazy(() => import("./components/requestLoader"));
const UserList = lazy(() => import("./components/userList"));
const ReqList = lazy(() => import("./components/requests"));
const Profile = lazy(() => import("./components/profile"));

//ova komponenta je dodana da se maknu ruzni crni okviri na svakom <a> i <button> sve dok se prvi put ne stisne TAB
const AccessibleFocusOutline = lazy(() =>
  import("./components/accessibleFocusOutline")
);

if (UserService.getUserContext() === null && !window.location.href.endsWith("login") && !window.location.href.endsWith("register")) {
  window.location.assign("/login");
}

LoginService.getCSRF()
    .then((response) => {
      console.log(response);
      console.log(response.data);
      console.log(response.headers);
    }).catch((error) => {
      console.log(error);
});

function App() {
  return (
      <BrowserRouter>
        <Suspense fallback={<div>Loading...</div>}>
          <AccessibleFocusOutline>
            <Switch>
            
              <Route path="/profile/:id" component={Profile} />
              <Route path="/requests" component={ReqList} />
              <Route path="/page" component={RequestLoader} />
              <Route path="/list" component={UserList} />
              <Route path="/home" component={Home} />
              <Route path="/login" component={Login} />
              <Route path="/register" exact component={Registration} />
              <Route path="/" component={Login} />
            </Switch>
          </AccessibleFocusOutline>
        </Suspense>
      </BrowserRouter>
  );
}

export default App;
