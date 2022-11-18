import React, { useState } from "react";
import style from "./style/log-reg.module.css";
import "../index.module.css";
import { Formik } from "formik";
import * as Yup from "yup";
import "bootstrap/dist/css/bootstrap.min.css";
import { Card } from "react-bootstrap";
import { Link } from "react-router-dom";
import LoginService from "../service/login-service";

export const Login = (props) => {
  const [myErrors, setMyErrors] = useState({});

  return (
    <div className={style.background}>
      <div className={style.empthy}></div>
      <div className="container">
        <Card className="crd col-lg-7 mx-auto">
          <Card.Title className={style.title}>
            Prijavi se u aplikaciju <span style={{ color: "red" }}>Pomozi</span>
            Mi
          </Card.Title>
          <Formik
            initialValues={{
              email: "",
              password: "",
            }}
            onSubmit={async (values) => {
              let formData = new FormData();

              formData.append("email", values.email);
              formData.append("password", values.password);

              try {
                await LoginService.login(formData);
                props.history.push("/home");
              } catch (error) {
                const code = error.response.status;
                const response = error.response.data;
                console.log(error.response.data);
                if (code === 401) {
                  setMyErrors({ response });
                }
              }

              /* .then(response => {
                console.log(response);
                //alert(JSON.stringify(response, null, 2));
                props.history.push("/home");
              })
            .catch((error1) => {
              const code = error1.response.status;
              const response = error1.response.data;
              console.log(error1.response.data)
              if (code === 401) {
                let div = document.createElement("div");
                div.innerHTML = response;
                document.getElementById("uncategorised").append(div);
              }
            }); */
            }}
            validationSchema={Yup.object().shape({
              email: Yup.string()
                .email("Unesite e-mail ispravnog formata")
                .required("Unesite e-mail"),
              password: Yup.string().required("Unesite zaporku"),
            })}
          >
            {(props) => {
              const {
                values,
                touched,
                errors,
                dirty,
                isSubmitting,
                handleChange,
                handleBlur,
                handleSubmit,
                handleReset,
              } = props;
              return (
                <form onSubmit={handleSubmit} className={style.form_fields}>
                  <div>
                    <div className={style.inp_line}>
                      <label className={style.label} htmlFor="email">
                        E-mail:
                      </label>
                      <input
                        id="email"
                        placeholder="E-mail"
                        type="text"
                        value={values.email}
                        onChange={handleChange}
                        onBlur={handleBlur}
                        className={
                          errors.email && touched.email
                            ? `${style.text_input} ${style.error}`
                            : style.text_input
                        }
                      />
                    </div>

                    {errors.email && touched.email && (
                      <div className={style.input_feedback}>{errors.email}</div>
                    )}

                    <div className={style.inp_line}>
                      <label className={style.label} htmlFor="password">
                        Zaporka:
                      </label>
                      <input
                        id="password"
                        placeholder="Zaporka"
                        type="password"
                        value={values.password}
                        onChange={handleChange}
                        onBlur={handleBlur}
                        className={
                          errors.password && touched.password
                            ? `${style.text_input} ${style.error}`
                            : style.text_input
                        }
                      />
                    </div>

                    {errors.password && touched.password && (
                      <div className={style.input_feedback}>
                        {errors.password}
                      </div>
                    )}
                  </div>

                  {myErrors && myErrors.response && (
                    <div className={style.input_feedback}>
                      {myErrors.response}
                    </div>
                  )}
                  <div className={(style.inp_line, style.lr_button_container)}>
                    <button
                      type="button"
                      className={style.buttonOutline}
                      onClick={handleReset}
                      disabled={!dirty || isSubmitting}
                    >
                      Resetiraj
                    </button>

                    <button
                      type="submit"
                      disabled={isSubmitting}
                      className={style.button}
                    >
                      Prijavi se
                    </button>
                  </div>
                </form>
              );
            }}
          </Formik>
          <div className={style.inp_line}>
            Niste registrirani?{" "}
            <Link to="/register">&nbsp;&nbsp;Registracija</Link>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default Login;
