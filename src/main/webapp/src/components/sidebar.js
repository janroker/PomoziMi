import NotificationsIcon from "@material-ui/icons/Notifications";
import Badge from "@material-ui/core/Badge";
import IconButton from "@material-ui/core/IconButton";
import Menu from "@material-ui/core/Menu";
import MenuItem from "@material-ui/core/MenuItem";
import React, { useState, useEffect } from "react";
import * as FaIcons from "react-icons/fa";
import * as AiIcons from "react-icons/ai";
import { Link } from "react-router-dom";
import { withStyles } from "@material-ui/core/styles";
import { SidebarData } from "./SidebarData";
import LogoutService from "../service/login-service";
import sidebarStyle from "./style/sidebar.module.css";
import { IconContext } from "react-icons";
import { Typography } from "@material-ui/core";
import UserService from "../service/user-service";
import Divider from "@material-ui/core/Divider";

const ITEM_HEIGHT = 48;

const StyledMenu = withStyles({
  paper: {
    border: "1px solid #d3d4d5",
  },
})((props) => (
  <Menu
    elevation={0}
    getContentAnchorEl={null}
    anchorOrigin={{
      vertical: "bottom",
      horizontal: "center",
    }}
    transformOrigin={{
      vertical: "top",
      horizontal: "center",
    }}
    {...props}
  />
));

const StyledMenuItem = withStyles((theme) => ({
  root: {
    "&:focus": {
      backgroundColor: theme.palette.primary.main,
      "& .MuiListItemIcon-root, & .MuiListItemText-primary": {
        color: theme.palette.common.white,
      },
    },
  },
}))(MenuItem);

function Navbar(props) {
  const [sidebar, setSidebar] = useState(false);
  const [value, setValue] = useState(0);
  const [anchorEl, setAnchorEl] = React.useState(null);
  const [notifs, setNotifs] = useState([]);
  const open = Boolean(anchorEl);

  const handleClick = (event) => {
    onNotifClick();
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    console.log("closing");
    setAnchorEl(null);
    let count = 0;
    for (let notif of notifs) {
      count++;
      UserService.setReadNotifs(notif.idNotification);
    }
    setValue(0);
  };

  const showSidebar = () => setSidebar(!sidebar);

  useEffect(() => {
    onNotifClick();
  }, []);

  const onNotifClick = () => {
    let userContext = UserService.getUserContext();
    if (userContext !== null) {
      const userId = UserService.getUserContext().id;
      UserService.getNotifications(userId)
        .then((response) => {
          let count = 0;
          if (response.data._embedded !== undefined) {
            console.log(response.data._embedded.notifications);
            setNotifs(response.data._embedded.notifications);
            console.log(response.data._embedded.notifications.length);
            for (let notif of response.data._embedded.notifications) {
              if (notif.received === false) {
                count++;
              }
            }
            setValue(count);
            console.log(response);
          }
        })
        .catch((error) => {
          alert(error);
        });
    }
  }

  const handleLogOut = () => {
    localStorage.removeItem("username");

    LogoutService.logout()
      .then((response) => {
        //props.history.push("/login");
        window.location.assign("/login");
      })
      .catch((error) => {
        console.log(error);
      });
  };

  const isLogout = (props, item) => {
    if (!(props === "Odjava")) {
      return <Link to={item.title === "Profil" ? item.path + "/" + UserService.getUserContext().id : item.path}>
        {item.icon}
        <span className={sidebarStyle.span_class}>{props}</span>
      </Link>
    } else {
      return <Link to="" onClick={handleLogOut}>{item.icon}<span className={sidebarStyle.span_class} onClick={handleLogOut}>{props}</span></Link>;
    }
  }

  if (UserService.getUserContext() === null) {
    window.location.assign("/login");
  }
  return (
    <>
      <IconContext.Provider value={{ color: "#000", size: "1em" }}>
        <div className={sidebarStyle.navbar}>
          <Link to="#" className={sidebarStyle.menu_bars}>
            <FaIcons.FaBars onClick={showSidebar} />
          </Link>

          <IconButton
            aria-label="show new notifications"
            color="inherit"
            onClick={(event) => handleClick(event)}
          >
            <Badge badgeContent={value} color="secondary">
              <NotificationsIcon />
            </Badge>
          </IconButton>
          <Menu
            id="long-menu"
            anchorEl={anchorEl}
            keepMounted
            open={open}
            onClose={() => handleClose()}
            PaperProps={{
              style: {
                maxHeight: ITEM_HEIGHT * 10,
                width: "70ch",
                whiteSpace: "break-spaces",
              },
            }}
          >
            {notifs.map((notif) => (
              <div key={notif.idNotification}>
                <MenuItem
                  style={{
                    whiteSpace: "break-spaces",
                  }}
                  onClick={() => handleClose()}
                >
                  {notif.message}
                </MenuItem>
                <Divider />
              </div>
            ))}
          </Menu>
          <a href="/home" style={{ textDecoration: "none" }}>
            <Typography
              variant="h4"
              color="textSecondary"
              align="left"
              display="inline"
            >
              Pomozi
            </Typography>
            <Typography variant="h4" color="secondary" display="inline">
              Mi
            </Typography>
          </a>
        </div>
        <nav
          className={
            sidebar
              ? `${sidebarStyle.nav_menu} ${sidebarStyle.active}`
              : sidebarStyle.nav_menu
          }
        >
          <span className={sidebarStyle.nav_menu_items} onClick={showSidebar}>
            <li className={sidebarStyle.navbar_toggle}>
              <Link to="#" className={sidebarStyle.menu_bars}>
                <AiIcons.AiOutlineLeft />
              </Link>
            </li>
            {SidebarData.map((item, index) => {
              let elem = isLogout(item.title, item);

              return (
                <li key={index} className={sidebarStyle.nav_text}>
                  {elem}
                </li>
              );
            })}
          </span>
        </nav>
      </IconContext.Provider>
    </>
  );
}

export default Navbar;
