const defaultTheme = require('tailwindcss/defaultTheme')
module.exports = {
  future: {
    removeDeprecatedGapUtilities: true,
  },
  theme: {
    extend:{
      colors:{
        veryDarkGray: "hsl(0, 0%, 17%)",
        darkGray: "hsl(0, 0%, 59%)"
      },
      fontFamily: {
        'Rubik': ['Rubik', 'sans-serif']
      }
    }
  }
};