import React, { useState } from "react";
import style from "./style/log-reg.module.css";
import "../index.module.css";
import { Formik } from "formik";
import * as Yup from "yup";
import "bootstrap/dist/css/bootstrap.min.css";
import { Card } from "react-bootstrap";
import { Link } from "react-router-dom";
import RegService from "../service/login-service";
import Geocode from "react-geocode";

Geocode.setApiKey(process.env.REACT_APP_GOOGLE_MAPS_API_KEY);

const checkAdress = (values) => {
  return (
    (values.town === null || values.town === "") &&
    (values.country === null || values.country === "") &&
    (values.address === null || values.address === "")
  );
};

const fieldSetter = (value) => {
  if (value === null) return "";
  return value;
};

const Registration = (props) => {
  const [myErrors, setMyErrors] = useState({});

  const submitFunction = async (props, values) => {
    let data = new FormData();

    let latitude = null,
      longitude = null;

    if (values.country || values.town || values.adress) {
      try {
        let response = await Geocode.fromAddress(
          values.country + " " + values.town + " " + values.address
        );
        console.log("aaaaaa", response);
        if (response.status === "ZERO_RESULTS") {
          setMyErrors({
            geocodeError:
              "Nemoguće je pronaći danu lokaciju. Molimo provjerite upisano.",
          });
          return;
        }
        const { lat, lng } = response.results[0].geometry.location;
        console.log(lat, lng);
        longitude = lng;
        latitude = lat;
      } catch (err) {
        setMyErrors({
          geocodeError:
            "Nemoguće je pronaći danu lokaciju. Molimo provjerite upisano.",
        });
        return;
      }
    }

    if (latitude != null && longitude != null && !checkAdress(values)) {
      data.append("adress", fieldSetter(values.address));
      data.append("state", fieldSetter(values.country));
      data.append("town", fieldSetter(values.town));
      data.append("latitude", latitude);
      data.append("longitude", longitude);
    }

    data.append("firstName", values.firstName);
    data.append("lastName", values.lastName);
    data.append("password", values.password);
    data.append("secondPassword", values.secondPassword);
    data.append("email", values.email);

    try {
      let res = await RegService.register(data);

      props.history.push("/login");
      console.log(res);
    } catch (err) {
      const code = err.response.status;
      const response = err.response.data;

      console.log(err.response);

      let backendErrors = {};

      if (code === 400) {
        for (let obj of response) {
          let message = obj.defaultMessage.split("] | [");

          backendErrors[obj.field ? obj.field : "uncategorised"] = message;
        }
      }
      if (code === 403) {
        backendErrors["uncategorised"] = response.message;
      }

      console.log(backendErrors);
      setMyErrors(backendErrors);
    }
  };

  return (
    <div className={style.background}>
      <div className={style.empthy1}></div>
      <div className={style.container}>
        <Card className="crd col-lg-7 mx-auto">
          <Card.Title className={style.title}>
            Izradite svoj <span style={{ color: "red" }}>Pomozi</span>Mi račun
          </Card.Title>
          <Formik
            initialValues={{
              firstName: "",
              lastName: "",
              email: "",
              password: "",
              secondPassword: "",
              country: "",
              town: "",
              address: "",
              error1: "",
            }}
            onSubmit={(values) => submitFunction(props, values)}
            validationSchema={Yup.object().shape({
              email: Yup.string()
                .email("Unesite e-mail ispravnog formata")
                .required("Unesite e-mail"),
              password: Yup.string()
                .min(8, "Zaporka mora imati barem 8 znakova")
                .required("Unesite zaporku"),
              secondPassword: Yup.string()
                .oneOf(
                  [Yup.ref("password")],
                  "Potvrda mora biti jednaka zaporci"
                )
                .required("Unesite potvrdu"),
              firstName: Yup.string()
                .min(2, "Prekratko ime")
                .required("Unesite ime"),
              lastName: Yup.string()
                .min(2, "Prekratko prezime")
                .required("Unesite prezime"),
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
                  <div className={style.inp_line}>
                    <label className={style.label}>Ime*:</label>
                    <input
                      id="firstName"
                      placeholder="Ime"
                      type="text"
                      value={values.firstName}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      className={
                        errors.firstName && touched.firstName
                          ? `${style.text_input} ${style.error}`
                          : style.text_input
                      }
                    />
                  </div>

                  {errors.firstName && touched.firstName && (
                    <div className={style.input_feedback}>
                      {errors.firstName}
                    </div>
                  )}
                  {myErrors && myErrors.firstName && (
                    <div className={style.input_feedback}>
                      {myErrors.firstName.map((part) => (
                        <div>{part}</div>
                      ))}
                    </div>
                  )}

                  <div className={style.inp_line}>
                    <label className={style.label}>Prezime*:</label>
                    <input
                      id="lastName"
                      placeholder="Prezime"
                      type="text"
                      value={values.lastName}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      className={
                        errors.lastName && touched.lastName
                          ? `${style.text_input} ${style.error}`
                          : style.text_input
                      }
                    />
                  </div>

                  {errors.lastName && touched.lastName && (
                    <div className={style.input_feedback}>
                      {errors.lastName}
                    </div>
                  )}
                  {myErrors && myErrors.lastName && (
                    <div className={style.input_feedback}>
                      {myErrors.lastName.map((part) => (
                        <div>{part}</div>
                      ))}
                    </div>
                  )}

                  <div className={style.inp_line}>
                    <label className={style.label}>E-mail*:</label>
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
                  {myErrors && myErrors.email && (
                    <div className={style.input_feedback}>
                      {myErrors.email.map((part) => (
                        <div>{part}</div>
                      ))}
                    </div>
                  )}

                  <div className={style.inp_line}>
                    <label className={style.label}>Zaporka*:</label>
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
                  {myErrors && myErrors.password && (
                    <div className={style.input_feedback}>
                      {myErrors.password.map((part) => (
                        <div>{part}</div>
                      ))}
                    </div>
                  )}

                  <div className={style.inp_line}>
                    <label className={style.label}>Potvrdi*:</label>
                    <input
                      id="secondPassword"
                      placeholder="Potvrdi"
                      type="password"
                      value={values.secondPassword}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      className={
                        errors.secondPassword && touched.secondPassword
                          ? `${style.text_input} ${style.error}`
                          : style.text_input
                      }
                    />
                  </div>

                  {errors.secondPassword && touched.secondPassword && (
                    <div className={style.input_feedback}>
                      {errors.secondPassword}
                    </div>
                  )}
                  {myErrors && myErrors.secondPassword && (
                    <div className={style.input_feedback}>
                      {myErrors.secondPassword.map((part) => (
                        <div>{part}</div>
                      ))}
                    </div>
                  )}

                  <div className={style.inp_line}>
                    <label className={style.label}>Država:</label>
                    <input
                      id="country"
                      placeholder="Država"
                      type="text"
                      value={values.country}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      className={
                        errors.country && touched.country
                          ? `${style.text_input} ${style.error}`
                          : style.text_input
                      }
                    />
                  </div>

                  {errors.country && touched.country && (
                    <div className={style.input_feedback}>{errors.country}</div>
                  )}
                  {myErrors && myErrors.state && (
                    <div className={style.input_feedback}>
                      {myErrors.state.map((part) => (
                        <div>{part}</div>
                      ))}
                    </div>
                  )}

                  <div className={style.inp_line}>
                    <label className={style.label}>Mjesto:</label>
                    <input
                      id="town"
                      placeholder="Mjesto"
                      type="text"
                      value={values.town}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      className={
                        errors.town && touched.town
                          ? `${style.text_input} ${style.error}`
                          : style.text_input
                      }
                    />
                  </div>

                  {errors.town && touched.town && (
                    <div className={style.input_feedback}>{errors.town}</div>
                  )}
                  {myErrors && myErrors.town && (
                    <div className={style.input_feedback}>
                      {myErrors.town.map((part) => (
                        <div>{part}</div>
                      ))}
                    </div>
                  )}

                  <div className={style.inp_line}>
                    <label className={style.label}>Adresa:</label>
                    <input
                      id="address"
                      placeholder="Adresa"
                      type="text"
                      value={values.address}
                      onChange={handleChange}
                      onBlur={handleBlur}
                      className={
                        errors.address && touched.address
                          ? `${style.text_input} ${style.error}`
                          : style.text_input
                      }
                    />
                  </div>

                  {errors.address && touched.address && (
                    <div className={style.input_feedback}>{errors.address}</div>
                  )}
                  {myErrors && myErrors.adress && (
                    <div className={style.input_feedback}>
                      {myErrors.adress.map((part) => (
                        <div>{part}</div>
                      ))}
                    </div>
                  )}

                  {myErrors && myErrors.geocodeError && (
                    <div className={style.input_feedback}>
                      {myErrors.geocodeError}
                    </div>
                  )}
                  {myErrors && myErrors.uncategorised && (
                    <div className={style.input_feedback}>
                      {myErrors.uncategorised.map((part) => (
                        <div>{part}</div>
                      ))}
                    </div>
                  )}
                  <div
                    className={`${style.inp_line} ${style.lr_button_container}`}
                  >
                    <span className={style.res_btn}>
                      <button
                        type="button"
                        className={style.buttonOutline}
                        onClick={handleReset}
                        disabled={!dirty || isSubmitting}
                      >
                        Resetiraj
                      </button>
                    </span>
                    <button
                      type="submit"
                      disabled={isSubmitting}
                      className={style.button}
                    >
                      Registracija
                    </button>
                  </div>
                </form>
              );
            }}
          </Formik>
          <div className={style.inp_line}>
            Već imate račun? <Link to="/login">&nbsp;&nbsp;Prijavite se </Link>
          </div>
        </Card>
      </div>
    </div>
  );
};

export default Registration;
