import React from 'react';
import './style/accessibleFocusOutline.css';

class AccessibleFocusOutline extends React.Component {
  state = {
    enableOutline: false,
  };

  componentDidMount() {
    window.addEventListener("keydown", this._handleKeydown);
  }

  _handleKeydown = (e) => {
    // Detect a keyboard user from a tab key press
    const isTabEvent = e.keyCode === 9;

    if (isTabEvent) {
      this.setState({ enableOutline: true });
    }
  };

  render() {
    return (
      <div className={this.state.enableOutline ? "" : "no-outline-on-focus"}>
        {this.props.children}
      </div>
    );
  }
}

export default AccessibleFocusOutline;
