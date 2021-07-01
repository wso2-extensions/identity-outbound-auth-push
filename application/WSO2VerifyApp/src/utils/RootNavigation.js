import * as React from 'react';

export const isReadyRef = React.createRef();

export const navigationRef = React.createRef();

export function navigate(name, params = null) {
  console.log('In root navigation');
  if (isReadyRef.current && navigationRef.current) {
    console.log('All checks passed');
    navigationRef.current.navigate(name, params);
  } else {
    // You can decide what to do if the app hasn't mounted
  }
}
