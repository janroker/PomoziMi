import React, { useState } from "react";
import { makeStyles } from "@material-ui/core/styles";
import "bootstrap/dist/css/bootstrap.min.css";
import "./style/page.module.css";
import { Formik } from "formik";
import Sidebar from "./sidebar";
import * as Yup from "yup";
import style from "./style/page.module.css";
import { GoogleMap, useLoadScript, Marker } from "@react-google-maps/api";
import Geocode from "react-geocode";
import LocationService from "../service/location-service";
import Service from "../service/login-service";
import Datetime from "react-datetime";
import "react-datetime/css/react-datetime.css";
import moment from "moment";
import "moment/locale/hr";
import Container from "@material-ui/core/Container";

Geocode.setApiKey(process.env.REACT_APP_GOOGLE_MAPS_API_KEY);

const useStyles = makeStyles((theme) => ({
  root: {
    boxShadow: "0 0 7px 0 rgba(0, 0, 0, 0.2)",
    padding: "5%",
    backgroundColor: "white",
  },
}));

const libraries = ["places"];
const mapContainerStyle = {
  height: "50vh",
  width: "100%",
};

const options = {
  disableDefaultUI: true,
  zoomControl: true,
};

const isValidDate = (current) => {
  return current.isAfter(moment().subtract(1, "day"));
};
const onFieldChange = (value, form) => {
  let dateTimeValue = value;

  if (value instanceof moment) {
    dateTimeValue = moment(value).format("YYYY-MM-DD HH:mm:ss");
  }

  form.setFieldValue("datetime", dateTimeValue);
};
const onFieldBlur = (form) => {
  form.setFieldTouched("datetime", true);
};

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

export const Dash = (props) => {
  const { isLoaded, loadError } = useLoadScript({
    googleMapsApiKey: process.env.REACT_APP_GOOGLE_MAPS_API_KEY,
    libraries,
  });
  const [long, setLong] = useState("");
  const [lat, setLat] = useState("");
  const [checked, setchecked] = useState(false);
  const [address, setAddress] = useState(false);
  const classes = useStyles();

  if (loadError) return "Error";
  if (!isLoaded) return "Loading...";

  document.body.style = "background-image: none;";

  const handleClick = () => {
    console.log(checked);
    if (checked === false) {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition, showError);
      } else {
        alert("Geolocation is not supported by this browser.");
      }
      setchecked(true);
    } else {
      setchecked(false);
      setLat("");
      setLong("");
    }
  };

  const showPosition = (position) => {
    console.log(position);

    setLong(position.coords.longitude);
    setLat(position.coords.latitude);

    Geocode.fromLatLng(
      position.coords.latitude,
      position.coords.longitude
    ).then(
      (response) => {
        const add = response.results[0].formatted_address;
        console.log(add);
        setAddress(add);
      },
      (error) => {
        console.error(error);
      }
    );
  };

  const showError = (error) => {
    switch (error.code) {
      case error.PERMISSION_DENIED:
        alert("User denied the request for Geolocation.");
        break;
      case error.POSITION_UNAVAILABLE:
        alert("Location information is unavailable.");
        break;
      case error.TIMEOUT:
        alert("The request to get user location timed out.");
        break;
      case error.UNKNOWN_ERROR:
        alert("An unknown error occurred.");
        break;
    }
    setLong(15.981919);
    setLat(45.815011);
  };

  return (
    <div className={style.background}>
      <Sidebar />
      <div>
        <div className={style.empthy1}> </div>
        <div className={style.container}>
          <Container
            maxWidth="md"
            disableGutters={true}
            className={classes.root}
          >
            <Formik
              initialValues={{
                address: "",
                phone: "",
                datetime: "",
                req: "",
                town: "",
                country: "",
              }}
              onSubmit={async (values) => {
                var call = new Object();
                var location = new Object();
                if (checked) {
                  if (address != null) {
                    var addParts = address.split(",");

                    if (addParts.length == 4) {
                      location.adress = addParts[0];
                      location.state = addParts[3].trim();
                      location.town =
                        addParts[1].trim() + ", " + addParts[2].trim();
                    } else if (addParts.length == 3) {
                      location.adress = addParts[0];
                      location.state = addParts[2].trim();
                      location.town = addParts[1].trim();
                    } else if (addParts.length == 2) {
                      location.town = addParts[0].trim();
                      location.country = addParts[1].trim();
                    } else if (addParts.length == 1)
                      location.country = addParts[0].trim();
                    else {
                      alert("Nije moguće postaviti danu lokaciju");
                    }

                    location.longitude = long;
                    location.latitude = lat;
                  } else alert("Nije moguće postaviti danu lokaciju");
                } else {
                  if (checkAdress(values)) location = null;
                  else {
                    try {
                      location = await LocationService.getLatLong(
                        fieldSetter(values.country),
                        fieldSetter(values.town),
                        fieldSetter(values.address)
                      );
                    } catch (err) {
                      alert(err);
                    }
                  }
                }
                call.phone = values.phone;
                call.location = location;
                call.tstmp = values.datetime === "" ? null : values.datetime;
                call.description = values.req;

                Service.sendRequest(call)
                  .then((response) => {
                    props.history.push("/home");
                  })
                  .catch((error) => {
                    console.warn(error.message);
                  });
                console.log(call);
              }}
              validationSchema={Yup.object().shape({
                req: Yup.string().required("Obavezno unesite zahtjev"),
                phone: Yup.number("Unos mora biti broj").required(
                  "Unesite broj mobitela"
                ),
                country: Yup.string(),
              })}
            >
              {(props) => {
                const {
                  values,
                  touched,
                  errors,
                  isSubmitting,
                  handleChange,
                  handleBlur,
                  handleSubmit,
                } = props;

                return (
                  <form onSubmit={handleSubmit}>
                    <div>
                      <div className={style.inp_line}>
                        <label className={style.label} htmlFor="phone">
                          *Broj mobitela:
                          <input
                            id="phone"
                            placeholder="Broj mobitela"
                            type="phone"
                            min="1"
                            max="10"
                            value={values.phone}
                            onChange={handleChange}
                            onBlur={handleBlur}
                            className={
                              errors.phone && touched.phone
                                ? `${style.text_input} ${style.error}`
                                : style.text_input
                            }
                          />{" "}
                          {errors.phone && touched.phone && (
                            <div className={style.input_feedback}>
                              {errors.phone}
                            </div>
                          )}
                        </label>
                      </div>

                      <div className={style.inp_line}>
                        <label className={style.label} htmlFor="req">
                          *Zahtjev:
                          <textarea
                            id="req"
                            placeholder="Opis zahtjeva"
                            type="text"
                            value={values.req}
                            onChange={handleChange}
                            onBlur={handleBlur}
                            className={
                              errors.req && touched.rew
                                ? `${style.text_input} ${style.error}`
                                : style.text_input
                            }
                            rows="3"
                          ></textarea>
                          {errors.req && touched.req && (
                            <div className={style.input_feedback}>
                              {errors.req}
                            </div>
                          )}
                        </label>
                      </div>

                      <fieldset>
                        <legend>Lokacija:</legend>
                        <div className={style.inp_line}>
                          <label className={style.label}>
                            Država:
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
                            {errors.country && touched.country && (
                              <div className={style.input_feedback}>
                                {errors.country}
                              </div>
                            )}
                          </label>
                        </div>

                        <div className={style.inp_line}>
                          <label className={style.label}>
                            Mjesto:
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
                          </label>
                        </div>

                        <div className={style.inp_line}>
                          <label className={style.label}>
                            Adresa:
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
                          </label>
                        </div>
                      </fieldset>

                      <div className={style.inp_line}>
                        <label className={style.label} htmlFor="datetime">
                          Rok izvršenja:
                        </label>
                        <Datetime
                          id="datetime"
                          name="datetime"
                          onChange={(value) => onFieldChange(value, props)}
                          onBlur={() => onFieldBlur(props)}
                          initialValue={moment(moment.now())}
                          dateFormat="YYYY-MM-DD"
                          locale="hr"
                          timeFormat="HH:mm:ss"
                          isValidDate={isValidDate}
                        />
                      </div>
                    </div>

                    <div className={style.inp_line}>
                      <label className={style.label} htmlFor="loc">
                        Označite ako želite dodati vašu trenutnu lokaciju:
                      </label>
                      <input
                        id="loc"
                        type="checkbox"
                        onClick={handleClick}
                      ></input>
                    </div>

                    {checked ? (
                      <Container maxWidth="md" disableGutters={true}>
                        <GoogleMap
                          mapContainerStyle={mapContainerStyle}
                          zoom={8}
                          center={{ lat: lat, lng: long }}
                          options={options}
                          onClick={(event) => {
                            setLat(event.latLng.lat());
                            setLong(event.latLng.lng());
                            Geocode.fromLatLng(
                              event.latLng.lat(),
                              event.latLng.lng()
                            ).then(
                              (response) => {
                                const add =
                                  response.results[0].formatted_address;
                                console.log(add);
                                setAddress(add);
                              },
                              (error) => {
                                console.error(error);
                              }
                            );
                          }}
                        >
                          <Marker key={16} position={{ lat: lat, lng: long }} />
                        </GoogleMap>
                      </Container>
                    ) : null}

                    <span
                      className={style.input_feedback}
                      id="uncategorised"
                    ></span>
                    <div
                      className={`${style.inp_line} ${style.lr_button_container}`}
                    >
                      <button
                        className={style.button}
                        type="submit"
                        disabled={isSubmitting}
                      >
                        Pošalji zahtjev
                      </button>
                    </div>
                  </form>
                );
              }}
            </Formik>
          </Container>
        </div>
      </div>
    </div>
  );
};

export default Dash;
