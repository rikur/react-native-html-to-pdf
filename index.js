import { NativeModules } from 'react-native';

const { RNHTMLtoPDF } = NativeModules;

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

const RNHTMLtoPDF2 = {
    page: {
        orientation: {
            Landscape: 'Landscape',
            Portrait: 'Portrait',
        },
    
        // Define page size constants in mm : w x h
        //
        // Convert to iOS point = 0.352778 * mm
        //
        // https://developer.android.com/reference/android/print/PrintAttributes.MediaSize.html
        //
        // Android page size units are mm
        //      https://developer.android.com/reference/android/print/PrintAttributes.MediaSize.html
        // iOS page size units are points
        // 
        // 1 mm = 2.834646 point; 1 point = 0.352778 mm
        //
        size: {
            A0: { id: 'A0', mm: { w: 841, h: 1189 }},
            A1: { id: 'A1', mm: { w: 594, h: 841 }},
            A2: { id: 'A2', mm: { w: 420, h: 594 }},
            A3: { id: 'A3', mm: { w: 297, h: 420 }},
            A4: { id: 'A4', mm: { w: 210, h: 297 }},
            A5: { id: 'A5', mm: { w: 148, h: 210 }},
            A6: { id: 'A6', mm: { w: 105, h: 148 }},
            A7: { id: 'A7', mm: { w: 74, h: 105 }},
            A8: { id: 'A8', mm: { w: 52, h: 74 }},
            UsGovernmentLetter: { id: 'UsGovernmentLetter', mm: { w: 279, h: 203 }},
            UsLetter: { id: 'UsLetter', mm: { w: 279, h: 216 }},
            UsLegal: { id: 'UsLegal', mm: { w: 356, h: 216 }},
        },
    },

    // Maintain default page options in JS 
    // to make easier to maintain
    convert(options) {
        if(!options.page) {
            options.page = {
                size: RNHTMLtoPDF2.page.size.UsLetter,
                orientation: RNHTMLtoPDF2.page.orientation.Portrait,
            };
        }
        if(!options.page.size) {
            options.page.size =  RNHTMLtoPDF2.page.size.UsLetter;
        }
        if(!options.page.orientation) {
            orientation = RNHTMLtoPDF2.page.orientation.Portrait;
        }
        return RNHTMLtoPDF.convert(options).then(result => {
            return result;
          });
    },
};

module.exports = {
  RNHTMLtoPDF,
  RNHTMLtoPDF2, // <- New export
  page,
}


