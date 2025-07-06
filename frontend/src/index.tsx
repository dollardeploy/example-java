import React, { ReactElement } from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import reportWebVitals from './reportWebVitals';
import HomeSection from './sections/HomeSection';

const renderElement = (rootElement: HTMLElement | null, reactElement: ReactElement) => {
  if (rootElement != null) {
    const root = ReactDOM.createRoot(rootElement);

    root.render(
      <React.StrictMode>
        {reactElement}
      </React.StrictMode>,
    );
  }
};

const sections: { [key: string]: JSX.Element }[] = 
[
  {
    'section-home': <HomeSection />
  }
]

sections.forEach((sectionObj) => {
  Object.keys(sectionObj).forEach((key) => {
    const element = document.getElementById(key);
    if (element) {
      renderElement(element, sectionObj[key]);
    }
  });
});

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
