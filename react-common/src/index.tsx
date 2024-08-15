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

renderElement(document.getElementById('section-home'), <HomeSection />);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
