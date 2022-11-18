import React, { useEffect } from "react";
import { makeStyles } from "@material-ui/core/styles";
import useMediaQuery from "@material-ui/core/useMediaQuery";
import clsx from "clsx";
import Card from "@material-ui/core/Card";
import Link from "@material-ui/core/Link";
import CardHeader from "@material-ui/core/CardHeader";
import CardContent from "@material-ui/core/CardContent";
import CardActions from "@material-ui/core/CardActions";
import Collapse from "@material-ui/core/Collapse";
import Avatar from "@material-ui/core/Avatar";
import IconButton from "@material-ui/core/IconButton";
import Typography from "@material-ui/core/Typography";
import { red } from "@material-ui/core/colors";
import ExpandMoreIcon from "@material-ui/icons/ExpandMore";
import Sidebar from "./sidebar";
import RequestService from "../service/request-service";
import Container from "@material-ui/core/Container";
import TextField from "@material-ui/core/TextField";
import Button from "@material-ui/core/Button";
import { GoogleMap, useLoadScript, Marker } from "@react-google-maps/api";
import DeleteIcon from "@material-ui/icons/Delete";
import UserService from "../service/user-service";
import style from "./style/log-reg.module.css";

if (UserService.getUserContext() === null) {
  window.location.assign("/login");
}

const useStyles = makeStyles((theme) => ({
  root: {
    width: '100vw',
    '@media (min-width: 680px)' : {
        width: '75vw'
    },
    boxShadow: "0 0 7px 0 rgba(0, 0, 0, 0.2)",
  },
  media: {
    height: 100,
    paddingTop: "56.25%", // 16:9
  },
  expand: {
    transform: "rotate(0deg)",
    marginLeft: "auto",
    transition: theme.transitions.create("transform", {
      duration: theme.transitions.duration.shortest,
    }),
  },
  expandOpen: {
    transform: "rotate(180deg)",
  },
  avatar: {
    backgroundColor: red[500],
  },
  page: {
    backgroundColor: "cornsilk",
    height: "100%",
    minHeight: "100vh",
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

export default function RecipeReviewCard(props) {
  useEffect(() => {
    RequestService.getRequests(1)
      .then((response) => {
        if (response.data._embedded !== undefined) {
          console.log(response.data._embedded.requests);
          setRequests(response.data._embedded.requests);
        }

        const roles = UserService.getUserContext().roles;
        for (let role of roles) {
          if (role === "ROLE_ADMIN") {
            setAdmin(true);
          }
        }
        //setUsersTemp(response.data._embedded.users);

        //rows = response.data._embedded.users;
        //console.log(rows);
        //console.log(rows[0]);
      })
      .catch((error) => {
        alert(error);
      });
  }, []);

  const { isLoaded, loadError } = useLoadScript({
    googleMapsApiKey: process.env.REACT_APP_GOOGLE_MAPS_API_KEY,
    libraries,
  });

  const handleDelete = (value) => () => {
    RequestService.deleteRequest(value.idRequest)
      .then((response) => {
        window.location.reload(true);
      })
      .catch((error) => {
        alert(error);
      });
  };

  const classes = useStyles();
  const [expanded, setExpanded] = React.useState([]);
  const [requests, setRequests] = React.useState([]);
  const [value, setValue] = React.useState(1);
  const [notSent, setNotSent] = React.useState(true);
  const [lat, setLat] = React.useState("");
  const [lng, setLng] = React.useState("");
  const [isAdmin, setAdmin] = React.useState(false);
  const [noLoc, setLoc] = React.useState(false);
  const [req, setReq] = React.useState();
  const [searchValue, setSearchValue] = React.useState("");
  const [isSubmitting, setIsSubmitting] = React.useState(false);

  useEffect(() => {
    RequestService.getRequests(1)
      .then((response) => {
        if (response.data._embedded != null) {
          setRequests(response.data._embedded.requests);
          const roles = UserService.getUserContext().roles;
          for (let role of roles) {
            if (role === "ROLE_ADMIN") {
              setAdmin(true);
            }
          }
          console.log(response.data._embedded.requests);
        }
      })
      .catch((error) => {
        alert(error);
      });
  }, []);

  useEffect(() => {
    requests.map(() => {
      setExpanded((old) => [...old, false]);
    });
  }, [requests]);

  if (loadError) return "Error";
  if (!isLoaded) return "Loading...";

  const handleSearchSubmit = (event) => {
    setIsSubmitting(true);
    RequestService.getSearchRequests(value, searchValue)
      .then((response) => {
        if (response.data._embedded != null) {
          console.log(response.data._embedded.requests);
          setRequests(response.data._embedded.requests);
        } else setRequests([]);
      })
      .catch((error) => {
        alert(error);
      });
    setIsSubmitting(false);
    event.preventDefault();
  };

  const handleChangeInput = (event) => {
    setValue(event.target.value);
  };

  const handleSearchChangeInput = (event) => {
    setSearchValue(event.target.value);
  };

  const refreshPage = () => {
    window.location.reload(true);
  };

  const handleExpandClick = (index) => {
    if (expanded == null || expanded.length === 0) return false;

    setExpanded((e) => {
      return e.map((item, i) => {
        if (i === index) return !item;
        return item;
      });
    });
  };

  const handleRequestClick = (value) => {
    console.log(value);

    RequestService.sendExecution(value.idRequest)
      .then((response) => {
        if (value.location !== null) {
          setLat(value.location.latitude);
          setLng(value.location.longitude);
          setReq(value);
          setNotSent(false);
          console.log(response);
        } else if (value.location === null) {
          setReq(value);
          setLoc(true);
          setNotSent(false);
        }
      })
      .catch((error) => {
      });
  };

  return (
    <div className={classes.page}>
      <Sidebar />
      <Container maxWidth="lg">
        {notSent && (
          <>
            <br></br>
            <Typography variant="body2" color="textSecondary" component="p">
              Pretraži zahtjeve po udaljenosti od vlastite lokacije u
              kilometrima.<br></br> (Ako korisnik nema zapisanu lokaciju
              prikazuju mu se samo zahtjevi bez lokacije.)
            </Typography>
            <form onSubmit={handleSearchSubmit}>
              <TextField
                id="filled-basic"
                label="Radius zahtjeva (km)"
                value={value}
                onChange={handleChangeInput}
                variant="filled"
              />
            <br></br>
            <Typography variant="body2" color="textSecondary" component="p">
              Pretraživanje po parametru autora:
            </Typography>
              <TextField
                id="filled-basic"
                label="ime, prezime i/ili email"
                value={searchValue}
                onChange={handleSearchChangeInput}
                variant="filled"
              />
              <button type="submit" disabled={isSubmitting} hidden={true}/>
            </form>
          </>
        )}
        {notSent && requests != null ? (
          requests.map((request, index) => (
            <div key = {request.idRequest}>
              <Card className={classes.root}>
                <CardHeader
                  avatar={
                    <Avatar>{request.author.firstName.substring(0, 1)}</Avatar>
                  }
                  title={
                    <Link
                      onClick={(event) => {
                        props.history.push("/profile/" + request.author.idUser);
                      }}
                    >
                      {request.author.firstName + " " + request.author.lastName}
                    </Link>
                  }
                  subheader={request.author.email}
                />
                <CardContent>
                  <Typography
                    variant="body2"
                    color="textSecondary"
                    component="p"
                  >
                    {request.description}
                  </Typography>
                </CardContent>
                <CardActions disableSpacing>
                  <Button
                    size="small"
                    onClick={() => handleRequestClick(request)}
                  >
                    Izvrši zahtjev
                  </Button>
                  {isAdmin ? (
                    <IconButton
                      aria-label="trash"
                      onClick={handleDelete(request)}
                    >
                      <DeleteIcon />
                    </IconButton>
                  ) : null}
                  <IconButton
                    className={clsx(classes.expand, {
                      [classes.expandOpen]: expanded[index],
                    })}
                    onClick={() => handleExpandClick(index)}
                    aria-expanded={expanded[index]}
                    aria-label="show more"
                  >
                    <ExpandMoreIcon />
                  </IconButton>
                </CardActions>
                <Collapse in={expanded[index]} timeout="auto" unmountOnExit>
                  <CardContent>
                    <Typography paragraph>Rok izvrsavanja:</Typography>

                    <Typography paragraph>
                      {request.tstmp === null
                        ? "Nije postavljen rok"
                        : request.tstmp}
                    </Typography>
                    <Typography paragraph>Lokacija:</Typography>

                    <Typography paragraph>
                      Grad:{" "}
                      {" " +
                        (request.location === null
                          ? "Grad nije zadan"
                          : request.location.town)}
                      <br></br>
                      Adresa:{" "}
                      {" " +
                        (request.location === null
                          ? "Adresa nije zadana"
                          : request.location.adress)}
                    </Typography>
                  </CardContent>
                </Collapse>
              </Card>
              <br></br>
            </div>
          ))
        ) : (
          <>
            <div>
              <button onClick={refreshPage} className={style.button}>
                Povratak na zahtjeve
              </button>
            </div>
            <h1>Zahtjev uspjesno poslan!</h1>
            <Typography paragraph>Zahtjev : {req.description}</Typography>
            {noLoc ? null : (
              <>
                <h2>Lokacija zahtjeva:</h2>

                <Container maxWidth="md" disableGutters={true}>
                  <GoogleMap
                    mapContainerStyle={mapContainerStyle}
                    zoom={15}
                    center={{ lat: lat, lng: lng }}
                    options={options}
                    onClick={(event) => {
                      setLat(event.latLng.lat());
                      setLng(event.latLng.lng());
                    }}
                  >
                    <Marker key={16} position={{ lat: lat, lng: lng }} />
                  </GoogleMap>
                </Container>
              </>
            )}
          </>
        )}
      </Container>
    </div>
  );
}
