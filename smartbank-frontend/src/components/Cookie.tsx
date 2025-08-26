const getSetCookie = {
  getCookie: (cname: string): string => {
    const name = `${cname}=`;
    const decodedCookie = decodeURIComponent(document.cookie);
    const cookieArray = decodedCookie.split(';');
    for (let cookie of cookieArray) {
      cookie = cookie.trim();
      if (cookie.startsWith(name)) {
        return cookie.substring(name.length);
      }
    }
    return '';
  },

  setCookie: (cname: string, cvalue: string, days?: number, minutes?: number): void => {
    const date = new Date();
    const durationInMinutes = (days ? days * 24 * 60 : 0) + (minutes ?? 0);
    date.setTime(date.getTime() + durationInMinutes * 60 * 1000);
    const expires = `expires=${date.toUTCString()}`;
    // document.cookie = `${cname}=${encodeURIComponent(cvalue)}; ${expires}; path=/; SameSite=Lax`;
    document.cookie = `${cname}=${cvalue}`;
  }
};

export default getSetCookie;
