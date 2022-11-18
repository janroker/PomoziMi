import React from "react";
import sliderStyle from "./style/slider.module.css";
import AliceCarousel from 'react-alice-carousel';
import "react-alice-carousel/lib/alice-carousel.css";
import image1 from '../images/cleaning.png'
import image2 from '../images/babysitting.png'
import image3 from '../images/driving.png'
import image4 from '../images/learning.png'
import image5 from '../images/pets.png'
import image6 from '../images/repair.png'
import image7 from '../images/shopping.png'
import image8 from '../images/washing.png'

export default function App() {
  return (
    <div className="App">
     <AliceCarousel infinite touchTracking autoPlayStrategy="none" disableButtonsControls autoPlay activeIndex autoPlayInterval="3000">
      <img src={image1} className={sliderStyle.sliderimg} alt=""/>
      <img src={image2} className={sliderStyle.sliderimg} alt=""/>
      <img src={image3} className={sliderStyle.sliderimg} alt=""/>
      <img src={image4} className={sliderStyle.sliderimg} alt=""/>
      <img src={image5} className={sliderStyle.sliderimg} alt=""/>
      <img src={image6} className={sliderStyle.sliderimg} alt=""/>
      <img src={image7} className={sliderStyle.sliderimg} alt=""/>
      <img src={image8} className={sliderStyle.sliderimg} alt=""/>
    </AliceCarousel>
    </div>
  );
}
