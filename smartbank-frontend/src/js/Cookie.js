const getSetCookie = {
    getCookie: cname => {
      if (typeof window === 'object') {
        const name = ${cname}=;
        const str = document.cookie;
        const uri_encoded = str.replace(/%([^\d].)/, "%25$1");
        const decodedCookie = decodeURIComponent(uri_encoded);
        const cookieSplit = decodedCookie.split(';');
        for (let index = 0; index < cookieSplit.length; index += 1) {
          let cookie = cookieSplit[index];
          while (cookie.charAt(0) === ' ') {
            cookie = cookie.substring(1);
          }
          if (cookie.indexOf(name) === 0) {
            return cookie.substring(name.length, cookie.length);
          }
        }
      }
      return '';
    },
  
    setCookie: (cname, cvalue, days, minutes) => {
      const date = new Date();
      const duration = (days ? days * 24 * 60 : 1 + minutes) * 60 * 1000;
      date.setTime(date.getTime() + duration);
      const expires = expires=${date.toUTCString()};
      
      // Use current domain, prefixed with a dot for subdomain support
      const currentDomain = window.location.hostname;
      const domain = domain=.${currentDomain};
  
      document.cookie = ${cname}=${cvalue};${expires};${domain};path=/;
    }
  };
  
  export default getSetCookie;