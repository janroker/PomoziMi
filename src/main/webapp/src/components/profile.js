import React, { useState, useEffect } from "react";
import { makeStyles, useTheme } from "@material-ui/core/styles";
import {
  Container,
  Typography,
  Avatar,
  List,
  ListItem,
  ListItemText,
  ListItemAvatar,
  Divider,
  Paper,
  IconButton,
  TextField,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Button,
  Tabs,
  Tab,
  Box,
} from "@material-ui/core";
//icons
import VisibilityIcon from "@material-ui/icons/Visibility";
import VisibilityOffIcon from "@material-ui/icons/VisibilityOff";
import ReportIcon from "@material-ui/icons/Report";
import ReportOffIcon from "@material-ui/icons/ReportOff";
import LocationOnIcon from "@material-ui/icons/LocationOn";
import StarBorderIcon from "@material-ui/icons/StarBorder";
import DoneIcon from "@material-ui/icons/Done";
import BlockIcon from "@material-ui/icons/Block";
import StarRateRoundedIcon from "@material-ui/icons/StarRateRounded";
import EqualizerIcon from "@material-ui/icons/Equalizer";
import Star from "@material-ui/icons/Star";
//services
import RatingService from "../service/rating-service";
import RequestService from "../service/request-service";
import CandidacyService from "../service/candidacy-service";
import UserService from "../service/user-service";
import LocationService from "../service/location-service";
//other
import "fontsource-roboto";
import PropTypes from "prop-types";
import SwipeableViews from "react-swipeable-views";
import Sidebar from "./sidebar";
import Rating from "@material-ui/lab/Rating";
import Tooltip from "@material-ui/core/Tooltip";

function TabPanel(props) {
  const { children, value, index, ...other } = props;

  return (
    <Typography
      component="div"
      role="tabpanel"
      hidden={value !== index}
      id={`full-width-tabpanel-${index}`}
      aria-labelledby={`full-width-tab-${index}`}
      {...other}
    >
      <Box>{children}</Box>
    </Typography>
  );
}

TabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.any.isRequired,
  value: PropTypes.any.isRequired,
};

function a11yProps(index) {
  return {
    id: `full-width-tab-${index}`,
    "aria-controls": `full-width-tabpanel-${index}`,
  };
}

const useStyles = makeStyles((theme) => ({
  profileInfo: {
    height: 300,
    width: "100%",
    padding: 20,
    display: "flex",
    alignItems: "center",
    boxShadow: "0 0 10px 0 rgba(0, 0, 0, 0.2)",
    marginBottom: 20,
  },

  avatarButton: {
    minWidth: 120,
    minHeight: 120,
    maxHeight: 250,
    maxWidth: 250,
    width: "17vw",
    height: "17vw",
    padding: 0,
    boxShadow: "0 0 10px 0 rgba(0, 0, 0, 0.2)",
  },

  avatar: {
    width: "100%",
    height: "100%",
  },

  about: {
    height: "100%",
    display: "flex",
    flexDirection: "column",
    justifyContent: "center",
    padding: "5%",
  },

  visibilityButton: {
    width: 40,
    height: 40,
    boxShadow: "0 0 10px 0 rgba(0, 0, 0, 0.2)",
  },

  visibilityLabel: {
    margin: 0,
  },

  visibilityRoot: {
    marginRight: "2vw",
  },

  body: {
    paddingLeft: 20,
    paddingRight: 20,
  },

  requestsContainer: {
    display: "flex",
    alignItems: "center",
    marginTop: 20,
    justifyContent: "center",
  },

  list: {
    width: "100%",
  },

  avatarLabel: {
    margin: 0,
    height: "100%",
  },

  tabs: {
    flexGrow: 1,
    boxShadow: "0 0 10px 0 rgba(0, 0, 0, 0.2)",
  },

  tabWrapper: {
    margin: 0,
  },

  scrollIndicator: {
    margin: 0,
  },

  ratingContainer: {
    marginBottom: "10px",
  },

  chainList: {
    width: "100%",
    display: "inline",
  },

  gridList: {
    flexWrap: "nowrap",
    transform: "translateZ(0)",
    justifyContent: "center",
  },

  chainContainer: {
    marginTop: 20,
    marginBottom: 20,
    paddingRight: "2vw",
    paddingLeft: "2vw",
  },

  chainTile: {
    width: "auto",
    boxShadow: "0 0 10px 0 rgba(0, 0, 0, 0.2)",
    padding: 5,
  },

  avatarFont: {
    fontSize: "6vw",
  },

  locationDialog: {
    display: "flex",
    flexDirection: "column",
  },

  locationInput: {
    margin: "10px",
  },
}));

const Profile = (props) => {
  const classes = useStyles();
  const theme = useTheme();
  document.body.style = "background-image: none; background-color: white";

  const [reqDialog, setReqDialog] = useState(false);
  const [deleteDialog, setDeleteDialog] = useState(false);
  const [locationDialog, setLocationDialog] = useState(false);
  const [gradeDialog, setGradeDialog] = useState({ req: null, open: false });
  const [about, setAbout] = useState(true);
  const [value, setValue] = useState(0);
  const [value2, setValue2] = useState(0);
  const [swipeable1, setSwipeable1] = useState(0);
  const [userData, setUserData] = useState(null);
  const [userStatistics, setUserStatistics] = useState(null);
  const [requestsByAuthor, setRequestsByAuthor] = useState(null);
  const [requestsByExecutor, setRequestsByExecutor] = useState(null);
  const [requests, setRequests] = useState(null);
  const [dialogReq, setDialogReq] = useState(null);
  const [updateReqs, setUpdateReqs] = useState({});
  const [isUser, setUser] = useState(false);
  const [isAdmin, setAdmin] = useState(false);
  const [isBlocked, setBlocked] = useState(false);
  const [rating, setRating] = useState({
    ratingComment: null,
    ratingGrade: null,
  });
  const [chainOfTrust, setChainOfTrust] = useState({});
  const [myErrors, setMyErrors] = useState({});
  const [isLocationSubmiting, setIsLocationSubmiting] = useState(false);
  const [location, setLocation] = useState({ state: "", town: "", adress: "" });

  function handleChange(event, newValue) {
    setValue(newValue);
  }

  function handleChangeIndex(index) {
    setValue(index);
  }

  function handleChange2(event, newValue) {
    setValue2(newValue);
  }

  function handleChangeIndex2(index) {
    setValue2(index);
  }

  function handleChange1(event, newValue) {
    setSwipeable1(newValue);
    console.log(newValue);
    setValue2(0);
  }

  const openReqDialog = () => {
    setReqDialog(true);
  };

  const closeReqDialog = () => {
    setReqDialog(false);
  };

  const openDeleteDialog = () => {
    setDeleteDialog(true);
  };

  const closeDeleteDialog = () => {
    setDeleteDialog(false);
  };

  const openGradeDialog = (request) => {
    setGradeDialog({ req: request, open: true });
  };

  const closeGradeDialog = () => {
    setGradeDialog({ req: null, open: false });
  };

  const openLocationDialog = () => {
    setLocationDialog(true);
  };

  const closeLocationDialog = () => {
    setLocationDialog(false);
  };

  const handleAbout = () => {
    setAbout(!about);
  };

  const handleBlock = (value) => () => {
    UserService.blockUser(props.match.params.id, value)
      .then((response) => {
        props.history.push("/list");
        console.log(response);
      })
      .catch((error) => {
        alert(error);
      });
  };

  var loggedInUser;

  const getLoggedInUser = () => {
    if (loggedInUser == null) loggedInUser = UserService.getUserContext();

    return loggedInUser;
  };

  const checkIfAuthor = (req) => {
    const user = getLoggedInUser();

    if (user == null) return false;

    if (req == null) return false;

    return user.id === req.author.idUser;
  };

  const checkUserDidNotRate = (req) => {
    const user = getLoggedInUser();

    if (req == null) return false;
    if (user == null) return false;

    if (req.ratings == null) return true;

    let res = true;
    req.ratings.map((rating) => {
      if (rating.rator.idUser == user.id) res = false;
    });

    return res;
  };

  const getRated = (req) => {
    const user = getLoggedInUser();

    if (user.id !== req.author.idUser) return req.author;

    return req.executor;
  };

  useEffect(() => {
    loggedInUser = UserService.getUserContext();
    const user = loggedInUser;
    UserService.getUser(props.match.params.id)
      .then((response) => {
        setUserData(response.data);
        if (response.data.enabled === false) {
          setBlocked(true);
        }
        if (user.id == props.match.params.id) {
          setUser(true);
        }
        for (let role of user.roles) {
          if (role === "ROLE_ADMIN") {
            setAdmin(true);
          }
        }
      })
      .catch((error) => {
        alert(error);
      });

    UserService.getUserStatistics(props.match.params.id)
      .then((response) => {
        setUserStatistics(response.data);
      })
      .catch((error) => {
        alert(error);
      });

    if (props.match.params.id == user.id) {
      UserService.getAuthored(user.id)
        .then((response) => {
          setRequestsByAuthor(response.data);
        })
        .catch((error) => {
          alert(error);
        });
    } else {
      setRequests();
    }

    if (props.match.params.id == user.id) {
      UserService.getByExecutor(user.id)
        .then((response) => {
          setRequestsByExecutor(response.data);
        })
        .catch((error) => {
          alert(error);
        });
    } else {
      setRequests();
    }

    UserService.getChainOfTrust(props.match.params.id)
      .then((response) => {
        setChainOfTrust(response.data);
      })
      .catch((error) => {
        alert(error);
      });
  }, [updateReqs, props.match.params.id]);

  useEffect(() => {
    setRequests(swipeable1 == 0 ? requestsByAuthor : requestsByExecutor);
  }, [swipeable1, requestsByAuthor, requestsByExecutor]);

  const mapRequests = (request) => {
    return (
      <div key={request.idRequest}>
        <ListItem
          // button={
          //   request.status === "ACTIVE" || request.status === "BLOCKED"
          //     ? true
          //     : false
          // }
          onClick={() => {
            setDialogReq(request);
            openReqDialog();
          }}
        >
          <ListItemAvatar>
            <Avatar>{request.author.firstName.substring(0, 1)}</Avatar>
          </ListItemAvatar>

          <ListItemText
            primary={request.author.email}
            secondary={
              <React.Fragment>
                <Typography
                  component="span"
                  variant="body2"
                  color="textPrimary"
                >
                  {request.author.firstName + " " + request.author.lastName}
                </Typography>
                {" — " + request.description}
              </React.Fragment>
            }
          />
          {request.status === "EXECUTING" &&
            request.confirmed == false &&
            checkIfAuthor(request) ? (
              <>
                <IconButton
                  color="primary"
                  classes={{
                    label: classes.visibilityLabel,
                    root: classes.visibilityRoot,
                  }}
                  onClick={(e) => {
                    RequestService.confirmRequest(request.idRequest)
                      .then(() => {
                        setUpdateReqs({});
                      })
                      .catch((error) => {
                        alert(error);
                      });
                    e.stopPropagation();
                  }}
                  className={classes.visibilityButton}
                >
                  <DoneIcon />
                </IconButton>
                <IconButton
                  color="secondary"
                  classes={{
                    label: classes.visibilityLabel,
                    root: classes.visibilityRoot,
                  }}
                  onClick={(e) => {
                    RequestService.blockRequest(request.idRequest)
                      .then(() => {
                        setUpdateReqs({});
                      })
                      .catch((error) => {
                        alert(error);
                      });
                    e.stopPropagation();
                  }}
                  className={classes.visibilityButton}
                >
                  <BlockIcon />
                </IconButton>
              </>
            ) : null}
          {request.status === "FINALIZED" && checkUserDidNotRate(request) ? (
            <IconButton
              color="primary"
              classes={{
                label: classes.visibilityLabel,
                root: classes.visibilityRoot,
              }}
              onClick={(e) => {
                openGradeDialog(request);
                e.stopPropagation();
              }}
              className={classes.visibilityButton}
            >
              <StarRateRoundedIcon />
            </IconButton>
          ) : null}
        </ListItem>
        <Divider variant="inset" component="li" />
      </div>
    );
  };

  const mapRatings = (rating) => {
    return (
      <div key={rating.idRating}>
        <ListItem alignItems="flex-start">
          <ListItemAvatar>
            <Avatar>{rating.rator.firstName.substring(0, 1)}</Avatar>
          </ListItemAvatar>
          <ListItemText
            primary={rating.rator.firstName + " " + rating.rator.lastName}
            secondary={
              <React.Fragment>
                <Typography
                  component="span"
                  variant="body2"
                  className={classes.inline}
                  color="textPrimary"
                >
                  Ocjena: {rating.rate}
                </Typography>
                {" — " + rating.comment}
              </React.Fragment>
            }
          />
        </ListItem>
        <Divider variant="inset" component="li" />
      </div>
    );
  };

  const shouldShowPhone = (req) => {
    if (checkIfAuthor(req)) return true;

    if (req == null) return false;

    if (req.confirmed === false) return false;

    return true;
  };

  return (
    <div>
      <Sidebar />

      {/*dialog for editing a request*/}
      <Dialog open={reqDialog} onClose={closeReqDialog} maxWidth="sm" fullWidth>
        <DialogTitle>
          Zahtjev od:{" "}
          {dialogReq
            ? dialogReq.author.firstName + " " + dialogReq.author.lastName
            : null}
        </DialogTitle>
        <DialogContent>
          <DialogContentText>
            STATUS: {dialogReq ? dialogReq.status : null}
            <br />
            DATUM I VRIJEME: {dialogReq ? dialogReq.tstmp : null}
            <br />
            DRŽAVA: {dialogReq ? dialogReq.location.state : null}
            <br />
            GRAD: {dialogReq ? dialogReq.location.town : null}
            <br />
            ADRESA: {dialogReq ? dialogReq.location.adress : null}
            <br />
            {shouldShowPhone(dialogReq) ? (
              <>
                KONTAKT:{" "}
                <a href={"tel:" + (dialogReq ? dialogReq.phone : null)}>
                  {dialogReq ? dialogReq.phone : null}
                </a>
                <br />
              </>
            ) : null}
            {dialogReq
              ? dialogReq.executor
                ? " POMOĆ JE PONUDIO: " +
                dialogReq.executor.firstName +
                " " +
                dialogReq.executor.lastName
                : null
              : null}
          </DialogContentText>
          <TextField
            label="Opis zahtjeva"
            defaultValue={dialogReq ? dialogReq.description : null}
            fullWidth
            multiline
            disabled={
              checkIfAuthor(dialogReq) && dialogReq.status === "ACTIVE"
                ? false
                : true
            }
            onChange={(description) =>
              (dialogReq.description = description.target.value)
            }
          />
        </DialogContent>
        {checkIfAuthor(dialogReq) ? (
          <DialogActions>
            {dialogReq.executor == null ? (
              <Button onClick={openDeleteDialog} color="secondary">
                Izbriši
              </Button>
            ) : null}
            {dialogReq.status !== "FINALIZED" ? (
              <Button
                onClick={() => {
                  closeReqDialog();
                  if (dialogReq) {
                    if (dialogReq.status === "ACTIVE") {
                      UserService.blockRequest(dialogReq.idRequest).then(
                        (response) => {
                          setRequests(response.data);
                          setUpdateReqs({});
                        }
                      );
                    } else if (dialogReq.status === "BLOCKED") {
                      UserService.unblockRequest(dialogReq.idRequest).then(
                        (response) => {
                          setRequests(response.data);
                          setUpdateReqs({});
                        }
                      );
                    }
                  }
                }}
                color="secondary"
              >
                {dialogReq
                  ? dialogReq.status === "ACTIVE" ||
                    dialogReq.status === "EXECUTING"
                    ? "Blokiraj"
                    : null
                  : null}
                {dialogReq
                  ? dialogReq.status === "BLOCKED"
                    ? "Aktiviraj"
                    : null
                  : null}
              </Button>
            ) : null}
            {dialogReq.status === "EXECUTING" && dialogReq.confirmed == true ? (
              <Button
                onClick={() => {
                  closeReqDialog();
                  RequestService.markExecuted(dialogReq.idRequest).then(
                    (response) => {
                      setUpdateReqs({});
                    }
                  );
                }}
                color="primary"
              >
                Označi izvšen
              </Button>
            ) : null}
            {dialogReq.status === "ACTIVE" ? (
              <Button
                onClick={() => {
                  closeReqDialog();
                  UserService.updateRequest(
                    dialogReq.idRequest,
                    dialogReq
                  ).then((response) => {
                    setRequests(response.data);
                    setUpdateReqs({});
                  });
                }}
                color="primary"
              >
                Spremi
              </Button>
            ) : null}
          </DialogActions>
        ) : null}
      </Dialog>

      {/*dialog for confirming deleting a request*/}
      <Dialog open={deleteDialog} onClose={closeDeleteDialog}>
        <DialogTitle>
          {"Jeste li sigurni da želite izbrisati ovaj zahtjev?"}
        </DialogTitle>
        <DialogActions>
          <Button
            onClick={() => {
              closeDeleteDialog();
              closeReqDialog();
              UserService.deleteRequest(dialogReq.idRequest).then(
                (response) => {
                  setRequests(response.data);
                  setUpdateReqs({});
                }
              );
            }}
            color="secondary"
          >
            Izbriši
          </Button>
          <Button onClick={closeDeleteDialog} color="primary">
            Odustani
          </Button>
        </DialogActions>
      </Dialog>

      {/*dialog for grading a user*/}
      <Dialog
        fullWidth
        maxWidth="sm"
        open={gradeDialog.open}
        onClose={closeGradeDialog}
      >
        <DialogTitle>
          Ocijenite korisnika:
          {gradeDialog.req
            ? " " +
            getRated(gradeDialog.req).firstName +
            " " +
            getRated(gradeDialog.req).lastName
            : userData
              ? " " + userData.firstName + " " + userData.lastName
              : null}
        </DialogTitle>
        <DialogContent style={{ justifyContent: "center" }}>
          <Rating
            name="customized-empty"
            value={rating.ratingGrade ? rating.ratingGrade : 0}
            precision={1}
            emptyIcon={<StarBorderIcon fontSize="large" />}
            icon={<Star fontSize="large" />}
            size="large"
            classes={{ root: classes.ratingContainer }}
            onChange={(event, grade) => {
              setRating({
                ratingComment: rating.ratingComment,
                ratingGrade: grade,
              });
            }}
          />
          <TextField
            value={rating.ratingComment ? rating.ratingComment : undefined}
            label="Komentar"
            fullWidth
            multiline
            onChange={(comment) => {
              setRating({
                ratingComment: comment.target.value,
                ratingGrade: rating.ratingGrade,
              });
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button
            disabled={rating.ratingGrade === null ? true : false}
            onClick={() => {
              if (gradeDialog.req == null)
                RatingService.rateUser(userData.idUser, {
                  comment: rating.ratingComment,
                  rate: rating.ratingGrade,
                }).then(res => {
                  setUpdateReqs({});
                  closeGradeDialog();
                });
              else
                RatingService.rateRequest(
                  getRated(gradeDialog.req).idUser,
                  gradeDialog.req.idRequest,
                  {
                    comment: rating.ratingComment,
                    rate: rating.ratingGrade,
                  }
                ).then((res) => {
                  setUpdateReqs({});
                  closeGradeDialog();
                });
            }}
            color="primary"
          >
            Ocijeni
          </Button>
          <Button onClick={closeGradeDialog} color="secondary">
            Odustani
          </Button>
        </DialogActions>
      </Dialog>

      {/*dialog for setting user location*/}
      <Dialog
        open={locationDialog}
        onClose={closeLocationDialog}
        maxWidth="xs"
        fullWidth
      >
        <DialogTitle>Uređivanje lokacije</DialogTitle>
        <DialogContent>
          <DialogContentText
            component="div"
            classes={{ root: classes.locationDialog }}
          >
            <TextField
              label="Država"
              onChange={(state) => {
                let loc = location;
                loc.state = state.target.value;
                setLocation(loc);
              }}
              classes={{ root: classes.locationInput }}
            />

            <TextField
              label="Grad"
              onChange={(town) => {
                let loc = location;
                loc.town = town.target.value;
                setLocation(loc);
              }}
              classes={{ root: classes.locationInput }}
            />

            <TextField
              label="Adresa"
              onChange={(adress) => {
                let loc = location;
                loc.adress = adress.target.value;
                setLocation(loc);
              }}
              classes={{ root: classes.locationInput }}
            />
            {myErrors && myErrors.message && <Typography variant="caption" color="secondary" style={{ marginBottom: 5 }}>{myErrors.message}</Typography>}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={closeLocationDialog} disabled={isLocationSubmiting} color="secondary">
            Odustani
          </Button>
          <Button
            disabled={isLocationSubmiting}
            onClick={async () => {
              setIsLocationSubmiting(true);
              if(myErrors && myErrors.message && setMyErrors({}));
              let usrData = userData;
              try {
                usrData.location = await LocationService.getLatLong(location.state, location.town, location.adress);
              } catch (err) {
                let message = err;
                setMyErrors({ message });
                setIsLocationSubmiting(false);
                return;
              }
              UserService.updateUser(usrData.idUser, usrData)
                .then((response) => {
                  console.log(response.data);
                  response ? setUserData(response.data) : setUserData(userData);
                })
                .catch((error) => {
                  setMyErrors(error); // TODO ??
                });
              setIsLocationSubmiting(false);
              closeLocationDialog();
            }}
            color="primary"
          >
            Spremi
          </Button>
        </DialogActions>
      </Dialog>

      <div className={classes.body}>
        <Container className={classes.profileInfo} maxWidth="lg">
          <IconButton
            className={classes.avatarButton}
            classes={{ label: classes.avatarLabel }}
            disabled={true}
          >
            <Avatar
              alt="avatar"
              className={classes.avatar}
              classes={{ root: classes.avatarFont }}
            >
              {userData
                ? userData.firstName.substring(0, 1) +
                userData.lastName.substring(0, 1)
                : null}
            </Avatar>
          </IconButton>

          <Container
            className={classes.about}
            maxWidth={false}
            disableGutters={true}
          >
            <Container maxWidth={false} disableGutters={true}>
              <Tooltip title="Prikaži dodatne izvještaje">
                <IconButton
                  classes={{
                    label: classes.visibilityLabel,
                    root: classes.visibilityRoot,
                  }}
                  onClick={handleAbout}
                  className={classes.visibilityButton}
                >
                  {about ? <VisibilityOffIcon /> : <VisibilityIcon />}
                </IconButton>
              </Tooltip>

              {isUser ? null : isAdmin ? (
                isBlocked ? (
                  <Tooltip title="Odblokiraj korisnika">
                    <IconButton
                      classes={{
                        label: classes.visibilityLabel,
                        root: classes.visibilityRoot,
                      }}
                      className={classes.visibilityButton}
                      onClick={handleBlock("true")}
                    >
                      <ReportOffIcon fontSize="large" />
                    </IconButton>
                  </Tooltip>
                ) : (
                    <Tooltip title="Blokiraj korisnika">
                      <IconButton
                        classes={{
                          label: classes.visibilityLabel,
                          root: classes.visibilityRoot,
                        }}
                        className={classes.visibilityButton}
                        onClick={handleBlock("false")}
                      >
                        <ReportIcon fontSize="large" />
                      </IconButton>
                    </Tooltip>
                  )
              ) : null}
              {!isUser ? (
                <Tooltip title="Ocijeni korisnika">
                  <IconButton
                    classes={{
                      label: classes.visibilityLabel,
                      root: classes.visibilityRoot,
                    }}
                    onClick={() => {
                      openGradeDialog(null);
                    }}
                    className={classes.visibilityButton}
                  >
                    <Star />
                  </IconButton>
                </Tooltip>
              ) : null}

              {isUser ? (
                <Tooltip title="Uredi svoju lokaciju">
                  <IconButton
                    classes={{
                      label: classes.visibilityLabel,
                      root: classes.visibilityRoot,
                    }}
                    className={classes.visibilityButton}
                    onClick={() => {
                      openLocationDialog();
                    }}
                  >
                    <LocationOnIcon />
                  </IconButton>
                </Tooltip>
              ) : null}
              {isUser ? (
                <Tooltip
                  title={
                    userStatistics && userStatistics.rank
                      ? "Odustani od kandidiranja"
                      : "Kandidiraj se"
                  }
                >
                  <IconButton
                    classes={{
                      label: classes.visibilityLabel,
                      root: classes.visibilityRoot,
                    }}
                    className={classes.visibilityButton}
                    onClick={() => {
                      userStatistics && userStatistics.rank
                        ? CandidacyService.deleteCandidacy()
                          .then((response) => {
                            console.log(response);
                            setUpdateReqs({});
                          })
                          .catch((error) => {
                            console.log(error);
                          })
                        : CandidacyService.candidateYourself()
                          .then((response) => {
                            console.log(response);
                            setUpdateReqs({});
                          })
                          .catch((error) => {
                            console.log(error);
                          });
                    }}
                    style={{
                      backgroundColor:
                        userStatistics && userStatistics.rank
                          ? "green"
                          : "white",
                    }}
                  >
                    <EqualizerIcon />
                  </IconButton>
                </Tooltip>
              ) : null}
            </Container>

            <Container maxWidth={false} disableGutters={true}>
              <Typography variant="h4" color="textSecondary">
                {userData ? userData.firstName + " " + userData.lastName : null}
              </Typography>
              <Typography
                variant="h5"
                color="textSecondary"
                style={{ display: about ? "flex" : "none" }}
              >
                {isUser ? (
                  <div>
                    Država: {userData && userData.location && userData.location.state}
                    <br />
                    Mjesto: {userData && userData.location && userData.location.town}
                    <br />
                    Adresa: {userData && userData.location && userData.location.adress}
                    <br />
                  </div>
                ) : null}
              </Typography>

              <Typography
                variant="h5"
                color="textSecondary"
                style={{ display: about ? "none" : "flex" }}
              >
                Ocjena:{" "}
                {userStatistics && userStatistics.avgGrade && +userStatistics.avgGrade.toFixed(3)}
                <br />
                Izvršeni zahtjevi:{" "}
                {userStatistics ? userStatistics.numExecutedR : null}
                <br />
                Zadani zahtjevi:{" "}
                {userStatistics ? userStatistics.numAuthoredR : null}
                <br />
                Rang: {userStatistics ? userStatistics.rank : null}
              </Typography>
            </Container>
          </Container>
        </Container>

        {!isUser ? null : (
          <Container
            className={classes.requestsContainer}
            maxWidth="lg"
            disableGutters={true}
          >
            <Paper className={classes.tabs}>
              <Tabs
                value={swipeable1}
                onChange={handleChange1}
                indicatorColor="primary"
                textColor="primary"
                variant="fullWidth"
                aria-label="full width tabs example"
                classes={{ indicator: classes.scrollIndicator }}
              >
                <Tab
                  label="Korisnik je autor"
                  {...a11yProps(0)}
                  classes={{ wrapper: classes.tabWrapper }}
                />
                <Tab
                  label="Korisnik je izvršitelj"
                  {...a11yProps(1)}
                  classes={{ wrapper: classes.tabWrapper }}
                />
              </Tabs>
            </Paper>
          </Container>
        )}
        {!isUser ? null : (
          <Container
            className={classes.requestsContainer}
            maxWidth="lg"
            disableGutters={true}
          >
            <Paper className={classes.tabs}>
              <Tabs
                value={swipeable1 == 0 ? value : value2}
                onChange={swipeable1 == 0 ? handleChange : handleChange2}
                indicatorColor="primary"
                textColor="primary"
                variant="fullWidth"
                aria-label="full width tabs example"
                classes={{ indicator: classes.scrollIndicator }}
              >
                {swipeable1 == 0 ? (
                  <Tab
                    label="Aktivni zahtjevi"
                    {...a11yProps(0)}
                    classes={{ wrapper: classes.tabWrapper }}
                  />
                ) : null}
                <Tab
                  label="Izvršeni zahtjevi"
                  {...a11yProps(swipeable1 == 0 ? 1 : 0)}
                  classes={{ wrapper: classes.tabWrapper }}
                />
                <Tab
                  label="Zahtjevi u obradi"
                  {...a11yProps(swipeable1 == 0 ? 2 : 1)}
                  classes={{ wrapper: classes.tabWrapper }}
                />
                <Tab
                  label="Blokirani zahtjevi"
                  {...a11yProps(swipeable1 == 0 ? 3 : 2)}
                  classes={{ wrapper: classes.tabWrapper }}
                />
              </Tabs>
            </Paper>
          </Container>
        )}
        {!isUser ? (
          <Container
            maxWidth="lg"
            disableGutters={true}
            className={classes.chainContainer}
          >
            <Typography
              variant="h5"
              color="secondary"
              style={{ marginBottom: 5 }}
            >
              Lanac povjerenja:
            </Typography>

            <List className={classes.list}>
              {chainOfTrust ? (
                chainOfTrust._embedded ? (
                  Object.keys(chainOfTrust._embedded).length === 0 &&
                    chainOfTrust._embedded.constructor === Object ? (
                      <div />
                    ) : (
                      chainOfTrust._embedded.ratings.map(mapRatings)
                    )
                ) : (
                    <div />
                  )
              ) : (
                  <div />
                )}
            </List>
          </Container>
        ) : (
            <Container
              className={classes.requestsContainer}
              maxWidth="lg"
              disableGutters={true}
            >
              {swipeable1 == 0 ? (
                <SwipeableViews
                  axis={theme.direction === "rtl" ? "x-reverse" : "x"}
                  index={value}
                  onChangeIndex={handleChangeIndex}
                  style={{ width: "100%" }}
                >
                  <TabPanel value={value} index={0} dir={theme.direction}>
                    <List className={classes.list}>
                      {requests
                        ? requests.ACTIVE
                          ? Object.keys(requests.ACTIVE).length === 0 &&
                            requests.ACTIVE.constructor === Object
                            ? null
                            : requests.ACTIVE._embedded.requests.map(mapRequests)
                          : null
                        : null}
                    </List>
                  </TabPanel>
                  <TabPanel value={value} index={1} dir={theme.direction}>
                    <List>
                      {requests
                        ? requests.FINALIZED
                          ? Object.keys(requests.FINALIZED).length === 0 &&
                            requests.FINALIZED.constructor === Object
                            ? null
                            : requests.FINALIZED._embedded.requests.map(
                              mapRequests
                            )
                          : null
                        : null}
                    </List>
                  </TabPanel>
                  <TabPanel value={value} index={2} dir={theme.direction}>
                    <List className={classes.list}>
                      {requests
                        ? requests.EXECUTING
                          ? Object.keys(requests.EXECUTING).length === 0 &&
                            requests.EXECUTING.constructor === Object
                            ? null
                            : requests.EXECUTING._embedded.requests.map(
                              mapRequests
                            )
                          : null
                        : null}
                    </List>
                  </TabPanel>
                  <TabPanel value={value} index={3} dir={theme.direction}>
                    <List className={classes.list}>
                      {requests
                        ? requests.BLOCKED
                          ? Object.keys(requests.BLOCKED).length === 0 &&
                            requests.BLOCKED.constructor === Object
                            ? null
                            : requests.BLOCKED._embedded.requests.map(mapRequests)
                          : null
                        : null}
                    </List>
                  </TabPanel>
                </SwipeableViews>
              ) : (
                  <SwipeableViews
                    axis={theme.direction === "rtl" ? "x-reverse" : "x"}
                    index={value2}
                    onChangeIndex={handleChangeIndex2}
                    style={{ width: "100%" }}
                  >
                    <TabPanel value={value2} index={0} dir={theme.direction}>
                      <List>
                        {requests
                          ? requests.FINALIZED
                            ? Object.keys(requests.FINALIZED).length === 0 &&
                              requests.FINALIZED.constructor === Object
                              ? null
                              : requests.FINALIZED._embedded.requests.map(
                                mapRequests
                              )
                            : null
                          : null}
                      </List>
                    </TabPanel>
                    <TabPanel value={value2} index={1} dir={theme.direction}>
                      <List className={classes.list}>
                        {requests
                          ? requests.EXECUTING
                            ? Object.keys(requests.EXECUTING).length === 0 &&
                              requests.EXECUTING.constructor === Object
                              ? null
                              : requests.EXECUTING._embedded.requests.map(
                                mapRequests
                              )
                            : null
                          : null}
                      </List>
                    </TabPanel>
                    <TabPanel value={value2} index={2} dir={theme.direction}>
                      <List className={classes.list}>
                        {requests
                          ? requests.BLOCKED
                            ? Object.keys(requests.BLOCKED).length === 0 &&
                              requests.BLOCKED.constructor === Object
                              ? null
                              : requests.BLOCKED._embedded.requests.map(mapRequests)
                            : null
                          : null}
                      </List>
                    </TabPanel>
                  </SwipeableViews>
                )}
            </Container>
          )}
      </div>
    </div>
  );
};

export default Profile;
