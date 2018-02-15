import { NativeModules } from 'react-native';

// What is cleanest way to export constants in a native module ?
const page = {

    orientation: {
        Landscape: 'Landscape',
        Portrait: 'Portrait',
    },

    // Maybe use these
    // https://developer.android.com/reference/android/print/PrintAttributes.MediaSize.html
    size: {
        A0: 'A0',
        A1: 'A1',
        A2: 'A2',
        A3: 'A3',
        A4: 'A4',
        A5: 'A5',
        A6: 'A6',
        A7: 'A7',
        A8: 'A8',
        UsGovernmentLetter: 'UsGovernmentLetter',
        UsLetter: 'UsLetter',
        UsLegal: 'UsLegal',
    },
};

const { RNHTMLtoPDF } = NativeModules;

module.exports = {
  RNHTMLtoPDF,
  page,
};
