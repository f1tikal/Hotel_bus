import React, { useMemo, useState } from 'react';
import { BrowserRouter, Link, NavLink, Route, Routes, useLocation, useNavigate, useParams } from 'react-router-dom';
import {
  CalendarDays,
  ChevronDown,
  ChevronLeft,
  ChevronRight,
  CircleUserRound,
  Heart,
  LogOut,
  Menu,
  Phone,
  Star,
  Trash2,
  UsersRound,
  X,
} from 'lucide-react';

const asset = (name) => `/assets/lemon-heritage/${name}`;
const pageAsset = (page, name) => `/assets/lemon-heritage/pages/${page}/${name}`;

const navLinks = [
  { label: 'Главная', to: '/' },
  { label: 'Номера', to: '/rooms' },
  { label: 'Избранное', to: '/favorites' },
  { label: 'Подробнее о нас', to: '/about' },
];

const rooms = [
  {
    id: 'aegean',
    title: 'Панорамный люкс «Aegean»',
    shortTitle: 'Панорамный люкс «Aegean»',
    meta: '45 м² · 2 гостя · Вид на Кальдеру',
    price: '17 500 руб.',
    priceValue: 17500,
    image: asset('room-aegean.png'),
    cardImage: pageAsset('rooms', 'aegean.png'),
    favoriteImage: pageAsset('favorites', 'aegean.png'),
    showcaseImage: asset('featured-suite.png'),
    description: 'Уединенное пространство с захватывающим видом на закат и Кальдеру.',
    longDescription:
      'Вид из окна на море просто сумасшедший. Номер стильный, белоснежный и минималистичный, ничего лишнего. Утренний кофе на балконе в тишине — ради этого стоит вернуться.',
    showcaseDescription:
      'Минималистичное пространство в традиционном кикладском стиле, созданное для созерцания. Интерьер из натурального льна и белёного дерева подчеркивает ослепительную синеву Эгейского моря за окном.',
    rating: 5,
  },
  {
    id: 'heritage-cave',
    title: '«The Heritage Cave» с бассейном',
    shortTitle: 'Гранд-люкс с бассейном «The Heritage Cave»',
    meta: '80 м² · 2 гостя · Инфинити-бассейн',
    price: '25 500 руб.',
    priceValue: 25500,
    image: asset('room-heritage-cave.png'),
    cardImage: pageAsset('rooms', 'heritage-cave.png'),
    favoriteImage: pageAsset('favorites', 'heritage-cave.png'),
    showcaseImage: asset('room-heritage-cave.png'),
    description: 'Королевский комфорт с персональным инфинити-бассейном под открытым небом.',
    longDescription:
      'Бассейн, уходящий из глубины пещеры прямо к обрыву — это что-то нереальное. Номер огромный, дает ощущение полной изоляции от остальных гостей.',
    showcaseDescription:
      'Просторный гранд-люкс с бассейном, созданный для полной приватности. Белёные своды, мягкий свет и открытая линия воды делают номер главным местом для долгого отдыха.',
    rating: 5,
  },
  {
    id: 'lemon-garden',
    title: 'Делюкс с террасой «Lemon Garden»',
    shortTitle: 'Делюкс с частной террасой «Lemon Garden»',
    meta: '55 м² · до 4 гостей · Вид на сад и море',
    price: '24 500 руб.',
    priceValue: 24500,
    image: asset('room-lemon-garden.png'),
    cardImage: pageAsset('rooms', 'lemon-garden.png'),
    favoriteImage: pageAsset('favorites', 'lemon-garden.png'),
    showcaseImage: asset('room-lemon-garden.png'),
    description: 'Уютный семейный люкс с деревянной террасой, утопающей в зелени лимонных деревьев.',
    longDescription:
      'Огромный плюс — своя зеленая терраса с отличным навесом от солнца. Внутри пещеры очень приятная прохлада. Завтраки приносили прямо к столику на улице с видом на море.',
    showcaseDescription:
      'Светлый делюкс с собственной террасой среди лимонной зелени. Здесь можно завтракать на солнце, прятаться в прохладе пещерных стен и видеть море прямо из приватной зоны отдыха.',
    rating: 4,
  },
  {
    id: 'canava',
    title: 'Уютный люкс «Canava»',
    shortTitle: 'Уютный люкс «Canava»',
    meta: '30 м² · 2 гостя · Бассейн',
    price: '16 000 руб.',
    priceValue: 16000,
    image: pageAsset('rooms', 'canava.png'),
    cardImage: pageAsset('rooms', 'canava.png'),
    favoriteImage: pageAsset('favorites', 'canava.png'),
    showcaseImage: pageAsset('rooms', 'canava.png'),
    description: 'Прохлада традиционных известковых сводов и мягкие лазурные акценты для вашего уединения.',
    longDescription:
      'Компактный номер с мягкими тканями, чистой геометрией и спокойным видом на белые террасы Санторини.',
    showcaseDescription:
      'Камерное пространство для пары: прохладные своды, мягкий текстиль, сине-белые акценты и спокойный ритм частного отдыха.',
    rating: 5,
  },
  {
    id: 'heritage-penthouse',
    title: 'Панорамный полулюкс «Heritage»',
    shortTitle: 'Панорамный полулюкс «Heritage»',
    meta: '38 м² · 3 гостя · Панорамные окна',
    price: '15 500 руб.',
    priceValue: 15500,
    image: pageAsset('rooms', 'heritage-penthouse.png'),
    cardImage: pageAsset('rooms', 'heritage-penthouse.png'),
    favoriteImage: pageAsset('favorites', 'heritage-penthouse.png'),
    showcaseImage: pageAsset('rooms', 'heritage-penthouse.png'),
    description: 'Уютное пространство с большими окнами и спокойной атмосферой кикладского дома.',
    longDescription: 'Светлый полулюкс для неспешного отдыха: мягкий текстиль, арочные окна и теплые детали ручной отделки.',
    showcaseDescription: 'Панорамные окна открывают мягкий свет Санторини, а натуральные материалы сохраняют ощущение прохлады и тишины.',
    rating: 5,
  },
  {
    id: 'azure-terrace',
    title: 'Люкс «Azure Terrace»',
    shortTitle: 'Люкс «Azure Terrace»',
    meta: '45 м² · 2 гостя · Вид на море',
    price: '16 000 руб.',
    priceValue: 16000,
    image: pageAsset('rooms', 'azure-terrace.png'),
    cardImage: pageAsset('rooms', 'azure-terrace.png'),
    favoriteImage: pageAsset('favorites', 'azure-terrace.png'),
    showcaseImage: pageAsset('rooms', 'azure-terrace.png'),
    description: 'Аутентичная кикладская комната с лазурными акцентами и просторным окном к морю.',
    longDescription: 'Терраса, мягкая кровать и глубокие синие акценты создают спокойное место для отдыха после прогулок по острову.',
    showcaseDescription: 'Azure Terrace соединяет белые своды, морской свет и приватность небольшой террасы.',
    rating: 5,
  },
  {
    id: 'mosaic-comfort',
    title: 'Номер «Mosaic Comfort»',
    shortTitle: 'Номер «Mosaic Comfort»',
    meta: '35 м² · 2 гостя · Синий декор',
    price: '10 500 руб.',
    priceValue: 10500,
    image: pageAsset('rooms', 'mosaic-comfort.png'),
    cardImage: pageAsset('rooms', 'mosaic-comfort.png'),
    favoriteImage: pageAsset('favorites', 'mosaic-comfort.png'),
    showcaseImage: pageAsset('rooms', 'mosaic-comfort.png'),
    description: 'Стильный интерьер с греческой мозаикой и мягким синим цветом.',
    longDescription: 'Компактный номер с выразительным керамическим акцентом и практичной планировкой для пары.',
    showcaseDescription: 'Мозаика, арочные ниши и спокойный свет делают номер камерным и запоминающимся.',
    rating: 4,
  },
  {
    id: 'santorini-breeze',
    title: 'Номер «Santorini Breeze»',
    shortTitle: 'Номер «Santorini Breeze»',
    meta: '40 м² · 2 гостя · Вид на море',
    price: '11 500 руб.',
    priceValue: 11500,
    image: pageAsset('rooms', 'santorini-breeze.png'),
    cardImage: pageAsset('rooms', 'santorini-breeze.png'),
    favoriteImage: pageAsset('favorites', 'santorini-breeze.png'),
    showcaseImage: pageAsset('rooms', 'santorini-breeze.png'),
    description: 'Светлый номер с прохладными синими ставнями и видом на море.',
    longDescription: 'Легкий, воздушный номер с белыми сводами и синей мебелью, которая поддерживает морскую палитру отеля.',
    showcaseDescription: 'Santorini Breeze создан для утреннего света и спокойного отдыха в бело-синей гамме острова.',
    rating: 5,
  },
  {
    id: 'aegean-breeze',
    title: 'Номер «Aegean Breeze»',
    shortTitle: 'Номер «Aegean Breeze»',
    meta: '32 м² · 2 гостя · Вид на море',
    price: '9 000 руб.',
    priceValue: 9000,
    image: pageAsset('rooms', 'aegean-breeze.png'),
    cardImage: pageAsset('rooms', 'aegean-breeze.png'),
    favoriteImage: pageAsset('favorites', 'aegean-breeze.png'),
    showcaseImage: pageAsset('rooms', 'aegean-breeze.png'),
    description: 'Светлый номер в оттенках традиционного декора и морского воздуха.',
    longDescription: 'Небольшой уютный номер для короткой поездки: мягкая кровать, светлые стены и вид на Эгейское море.',
    showcaseDescription: 'Aegean Breeze дает базовый комфорт Lemon Heritage без лишней декоративности.',
    rating: 4,
  },
  {
    id: 'cave-mythos',
    title: 'Полулюкс «Cave Mythos»',
    shortTitle: 'Полулюкс «Cave Mythos»',
    meta: '48 м² · 4 гостя · Вид на море',
    price: '16 300 руб.',
    priceValue: 16300,
    image: pageAsset('rooms', 'cave-mythos.png'),
    cardImage: pageAsset('rooms', 'cave-mythos.png'),
    favoriteImage: pageAsset('favorites', 'cave-mythos.png'),
    showcaseImage: pageAsset('rooms', 'cave-mythos.png'),
    description: 'Просторный номер с арочными сводами и семейной зоной отдыха.',
    longDescription: 'Пещерная архитектура, светлая палитра и отдельная зона отдыха для семьи или небольшой компании.',
    showcaseDescription: 'Cave Mythos подчеркивает традиционную форму санторинских пещерных домов и добавляет больше пространства.',
    rating: 5,
  },
];

const getRoomById = (roomId) => rooms.find((room) => room.id === roomId) || rooms[0];
const toDate = (value) => new Date(`${value}T12:00:00`);
const getNights = (checkIn, checkOut) => Math.max(1, Math.round((toDate(checkOut) - toDate(checkIn)) / 86400000));
const formatMoney = (value) => `${value.toLocaleString('ru-RU')} руб.`;

const bookingHistory = [
  {
    id: 1,
    roomId: 'aegean',
    title: 'Панорамный люкс «Aegean»',
    dates: '17.04.26 - 22.04.26',
    status: 'Заезд и выезд в 12:00',
    price: '87 500 руб. - оплачено',
    note: 'Завтрак включен',
    image: asset('room-aegean.png'),
  },
  {
    id: 2,
    roomId: 'heritage-cave',
    title: '«The Heritage Cave» с бассейном',
    dates: '17.04.26 - 22.04.26',
    status: 'Подтверждено',
    price: '124 000 руб. - оплачено',
    note: 'Трансфер по запросу',
    image: asset('room-heritage-cave.png'),
  },
];

function Button({ children, variant = 'primary', className = '', ...props }) {
  return (
    <button className={`button button--${variant} ${className}`} {...props}>
      {children}
    </button>
  );
}

function Header() {
  const [open, setOpen] = useState(false);

  return (
    <header className="site-header">
      <div className="header-ornaments" aria-hidden="true">
        <img src={asset('ornament-left.svg')} alt="" />
        <img src={asset('ornament-right.svg')} alt="" />
      </div>
      <div className="header-inner">
        <button className="icon-button menu-button" onClick={() => setOpen(true)} aria-label="Открыть меню">
          <Menu size={24} />
        </button>
        <nav className="desktop-nav desktop-nav--left" aria-label="Основная навигация">
          {navLinks.slice(0, 3).map((link) => (
            <NavItem key={link.label} {...link} />
          ))}
        </nav>
        <Link className="logo-link" to="/" aria-label="Aegean Lemon Heritage">
          <img src={asset('logo.png')} alt="Aegean Lemon Heritage" />
        </Link>
        <nav className="desktop-nav desktop-nav--right" aria-label="Дополнительная навигация">
          <NavItem {...navLinks[3]} />
        </nav>
        <Link className="login-link" to="/login">
          Войти
          <CircleUserRound size={22} />
        </Link>
      </div>
      <div className={`mobile-panel ${open ? 'is-open' : ''}`} aria-hidden={!open}>
        <div className="mobile-panel__top">
          <img src={asset('logo.png')} alt="Aegean Lemon Heritage" />
          <button className="icon-button" onClick={() => setOpen(false)} aria-label="Закрыть меню">
            <X size={24} />
          </button>
        </div>
        {navLinks.map((link) => (
          <NavItem key={link.label} {...link} onClick={() => setOpen(false)} />
        ))}
        <Link className="mobile-login" to="/login" onClick={() => setOpen(false)}>
          Войти в аккаунт
        </Link>
      </div>
    </header>
  );
}

function NavItem({ to, label, onClick }) {
  return (
    <NavLink to={to} onClick={onClick} end={to === '/'}>
      {label}
    </NavLink>
  );
}

function Footer() {
  return (
    <footer className="footer">
      <div className="footer-top">
        <div className="footer-column footer-column--right">
          <strong>Отель</strong>
          <Link to="/rooms">Номера</Link>
          <Link to="/lemon-gardens">Лимонные сады</Link>
          <Link to="/gastronomy">Гастрономия</Link>
        </div>
        <img className="footer-logo" src={asset('logo-footer.png')} alt="Aegean Lemon Heritage" />
        <div className="footer-column">
          <strong>Помощь</strong>
          <Link to="/rooms">Бронирование</Link>
          <Link to="/favorites">Избранное</Link>
          <Link to="/account">Личный кабинет</Link>
        </div>
      </div>
      <div className="footer-contacts">
        <strong>Свяжитесь с нами</strong>
        <span className="socials" aria-label="Социальные сети">
          <img src={asset('instagram.png')} alt="Instagram" />
          <img src={asset('facebook.png')} alt="Facebook" />
          <img src={asset('telegram.png')} alt="Telegram" />
        </span>
        <a href="tel:+302286000000">+30 22860 XXXXX</a>
        <a href="mailto:hello@lemonheritage.com">hello@lemonheritage.com</a>
      </div>
    </footer>
  );
}

function PageShell({ children }) {
  return (
    <>
      <Header />
      <main>{children}</main>
      <Footer />
    </>
  );
}

function OrnamentDivider() {
  return (
    <div className="ornament-divider" aria-hidden="true">
      <img src={asset('ornament-long.svg')} alt="" />
    </div>
  );
}

function PageHero({ image, title, align = 'right' }) {
  return (
    <section className={`page-hero page-hero--${align}`}>
      <img src={image} alt="" />
      <div className="page-hero__shade" />
      <h1>{title}</h1>
    </section>
  );
}

function BookingSearch() {
  const [openField, setOpenField] = useState(null);
  const [values, setValues] = useState({
    checkIn: '2027-01-01',
    checkOut: '2027-01-02',
    guests: 2,
    category: 'Люкс',
  });

  const formattedDate = (value) => {
    const [year, month, day] = value.split('-');
    return `${day}.${month}.${year.slice(2)}`;
  };
  const close = () => setOpenField(null);
  const changeValue = (key, value) => setValues((current) => ({ ...current, [key]: value }));

  return (
    <form id="booking" className="booking-search" onSubmit={(event) => event.preventDefault()}>
      <BookingField
        id="checkIn"
        icon={<CalendarDays size={18} />}
        label="Заезд"
        value={formattedDate(values.checkIn)}
        openField={openField}
        setOpenField={setOpenField}
      >
        <CalendarPopover value={values.checkIn} onChange={(value) => changeValue('checkIn', value)} onClose={close} />
      </BookingField>
      <BookingField
        id="checkOut"
        icon={<CalendarDays size={18} />}
        label="Выезд"
        value={formattedDate(values.checkOut)}
        openField={openField}
        setOpenField={setOpenField}
      >
        <CalendarPopover value={values.checkOut} onChange={(value) => changeValue('checkOut', value)} onClose={close} />
      </BookingField>
      <BookingField
        id="guests"
        icon={<UsersRound size={18} />}
        label="Гости"
        value={values.guests}
        dropdown
        openField={openField}
        setOpenField={setOpenField}
      >
        <ChoicePopover
          options={[1, 2, 3, 4].map((guests) => ({ label: `${guests} ${guests === 1 ? 'гость' : 'гостя'}`, value: guests }))}
          value={values.guests}
          onChange={(value) => {
            changeValue('guests', value);
            close();
          }}
        />
      </BookingField>
      <BookingField
        id="category"
        label="Категория"
        value={values.category}
        dropdown
        openField={openField}
        setOpenField={setOpenField}
      >
        <ChoicePopover
          options={['Люкс', 'Делюкс', 'Гранд-люкс'].map((category) => ({ label: category, value: category }))}
          value={values.category}
          onChange={(value) => {
            changeValue('category', value);
            close();
          }}
        />
      </BookingField>
      <Button type="submit" className="booking-search__submit">
        Найти номер
      </Button>
    </form>
  );
}

function BookingField({ id, icon, label, value, dropdown = false, openField, setOpenField, children }) {
  const isOpen = openField === id;

  return (
    <div className="booking-field">
      <span>{label}</span>
      <button type="button" className={isOpen ? 'is-open' : ''} onClick={() => setOpenField(isOpen ? null : id)}>
        {icon}
        {value}
        {dropdown && <ChevronDown size={16} />}
      </button>
      {isOpen && children}
    </div>
  );
}

function CalendarPopover({ value, onChange, onClose }) {
  return (
    <div className="booking-popover booking-popover--calendar">
      <input type="date" value={value} onChange={(event) => onChange(event.target.value)} />
      <Button type="button" onClick={onClose}>
        Готово
      </Button>
    </div>
  );
}

function ChoicePopover({ options, value, onChange }) {
  return (
    <div className="booking-popover">
      {options.map((option) => (
        <button
          key={option.value}
          type="button"
          className={option.value === value ? 'is-selected' : ''}
          onClick={() => onChange(option.value)}
        >
          {option.label}
        </button>
      ))}
    </div>
  );
}

function HomePage() {
  const [featuredIndex, setFeaturedIndex] = useState(0);
  const [roomsOffset, setRoomsOffset] = useState(0);
  const featuredRoom = rooms[featuredIndex];
  const orderedRooms = [...rooms.slice(roomsOffset), ...rooms.slice(0, roomsOffset)].slice(0, 3);
  const goFeatured = (direction) => setFeaturedIndex((current) => (current + direction + rooms.length) % rooms.length);
  const goRooms = (direction) => setRoomsOffset((current) => (current + direction + rooms.length) % rooms.length);

  return (
    <PageShell>
      <section className="hero">
        <img className="hero__image" src={asset('hero.png')} alt="Бассейн с видом на Эгейское море" />
        <div className="hero__overlay" />
        <div className="hero__content">
          <p>Aegean Lemon Heritage.</p>
          <h1>Живая история Санторини.</h1>
          <Link className="button button--light" to="/rooms">
            Каталог номеров
          </Link>
        </div>
      </section>
      <OrnamentDivider />
      <section id="about" className="section section--about">
        <div className="section__copy">
          <h2>О нас</h2>
          <p>Aegean Lemon Heritage воплощает в себе подлинный дух Санторини, бережно храня традиции островной архитектуры в каждой детали.</p>
          <p>Мы бережно восстановили традиционные пещерные дома, сохранив их уникальную архитектуру и дополнив её современным комфортом.</p>
        </div>
        <img className="rounded-image" src={asset('about.png')} alt="Белая терраса с видом на море" />
      </section>
      <section className="featured-room">
        <img src={featuredRoom.showcaseImage} alt={featuredRoom.title} />
        <div className="featured-room__content">
          <h2>{featuredRoom.title}</h2>
          <p className="room-meta">{featuredRoom.meta}</p>
          <p>{featuredRoom.showcaseDescription}</p>
          <div className="slider-dots" aria-label="Слайды номера">
            <button type="button" onClick={() => goFeatured(-1)} aria-label="Предыдущий номер">
              <ChevronLeft size={30} />
            </button>
            {rooms.slice(0, 3).map((room, index) => (
              <button key={room.id} type="button" className={index === featuredIndex ? 'is-active' : ''} onClick={() => setFeaturedIndex(index)} aria-label={`Показать ${room.title}`} />
            ))}
            <button type="button" onClick={() => goFeatured(1)} aria-label="Следующий номер">
              <ChevronRight size={30} />
            </button>
          </div>
          <Link className="button button--light" to="/rooms">
            Каталог номеров
          </Link>
        </div>
      </section>
      <BookingSearch />
      <section id="gastronomy" className="section section--gastronomy">
        <img className="rounded-image" src={asset('gastronomy.png')} alt="Греческий салат и лимонад на террасе" />
        <div className="section__copy">
          <h2>Гастрономия: от сада до стола</h2>
          <p>Мы верим, что история острова раскрывается через его вкусы.</p>
          <p>Утро в Lemon Heritage начинается с аромата свежей выпечки и нашего фирменного лимонада, приготовленного из плодов, собранных в саду на рассвете.</p>
          <Link className="text-link" to="/gastronomy">Открыть гастрономию</Link>
        </div>
      </section>
      <OrnamentDivider />
      <section id="rooms" className="rooms-section">
        <button className="carousel-arrow" type="button" onClick={() => goRooms(-1)} aria-label="Предыдущие номера">
          <ChevronLeft size={34} />
        </button>
        <div className="rooms-grid">
          {orderedRooms.map((room) => (
            <RoomReviewCard key={room.id} room={room} />
          ))}
        </div>
        <button className="carousel-arrow" type="button" onClick={() => goRooms(1)} aria-label="Следующие номера">
          <ChevronRight size={34} />
        </button>
      </section>
      <section className="location-section">
        <img src={asset('location-map.png')} alt="Карта расположения отеля в Акротири" />
        <div>
          <h2>Тихая гавань в Акротири</h2>
          <p>Мы выбрали южную часть острова, чтобы вы могли насладиться тишиной вдали от туристической суеты.</p>
          <p>Дорога до аэропорта или порта на автомобиле составит всего 20 минут.</p>
          <Link className="button button--light" to="/rooms">
            Каталог номеров
          </Link>
        </div>
      </section>
    </PageShell>
  );
}

function RoomReviewCard({ room }) {
  return (
    <article className="room-card" id={room.id === 'aegean' ? 'favorites' : undefined}>
      <img src={room.image} alt={room.title} />
      <div>
        <h3>{room.shortTitle}</h3>
        <p>{room.longDescription}</p>
      </div>
      <div className="stars" aria-label={`Рейтинг ${room.rating} из 5`}>
        {Array.from({ length: 5 }, (_, index) => (
          <Star key={index} size={34} fill={index < room.rating ? 'currentColor' : 'none'} />
        ))}
      </div>
    </article>
  );
}

function RoomBookingCard({ room, favorite = false, onToggleFavorite, imageVariant = 'card' }) {
  const [booked, setBooked] = useState(false);
  const navigate = useNavigate();
  const image = imageVariant === 'favorite' ? room.favoriteImage : room.cardImage;

  return (
    <article className="booking-room-card">
      <Link className="booking-room-card__media" to={`/rooms/${room.id}`}>
        <img src={image} alt={room.title} />
      </Link>
      <div className="booking-room-card__body">
        <Link to={`/rooms/${room.id}`}>
          <h2>{room.title}</h2>
        </Link>
        <p className="room-meta-line">{room.meta}</p>
        <p>{room.description}</p>
        <strong>{room.price}</strong>
        <div className="booking-room-card__actions">
          <button type="button" className={`heart-button ${favorite ? 'is-active' : ''}`} onClick={() => onToggleFavorite?.(room.id)} aria-label="Добавить в избранное">
            <Heart size={34} fill={favorite ? 'currentColor' : 'none'} />
          </button>
          <button type="button" className="pill-action" onClick={() => {
            setBooked(true);
            navigate(`/rooms/${room.id}`);
          }}>
            {booked ? 'Выбрано' : 'Забронировать'}
          </button>
        </div>
      </div>
    </article>
  );
}

function RoomsPage() {
  const [favorites, setFavorites] = useState(new Set(['aegean']));
  const toggleFavorite = (id) => setFavorites((current) => {
    const next = new Set(current);
    next.has(id) ? next.delete(id) : next.add(id);
    return next;
  });

  return (
    <PageShell>
      <PageHero image={pageAsset('rooms', 'hero.png')} title="Ваш личный вид на Кальдеру: пещерные дома, где время замирает" />
      <OrnamentDivider />
      <section className="page-intro page-intro--rooms">
        <div>
          <h1>В каждом нашем номере оживает история Санторини.</h1>
          <p>Мы бережно отреставрировали традиционные кикладские пещеры, сохранив их прохладу и уникальную геометрию, и дополнили их современным комфортом.</p>
        </div>
        <img src={pageAsset('rooms', 'key.png')} alt="Ключ Aegean Lemon Heritage" />
      </section>
      <BookingSearch />
      <section className="rooms-catalog">
        <div className="rooms-catalog__grid">
          {rooms.map((room) => (
            <RoomBookingCard key={room.id} room={room} favorite={favorites.has(room.id)} onToggleFavorite={toggleFavorite} />
          ))}
        </div>
      </section>
    </PageShell>
  );
}

function FavoritesPage() {
  const [favorites, setFavorites] = useState(new Set(rooms.map((room) => room.id)));
  const toggleFavorite = (id) => setFavorites((current) => {
    const next = new Set(current);
    next.has(id) ? next.delete(id) : next.add(id);
    return next;
  });

  return (
    <PageShell>
      <PageHero image={pageAsset('favorites', 'hero.png')} title="Персональная коллекция лучших номеров" />
      <OrnamentDivider />
      <section className="favorite-grid">
        {rooms.map((room) => (
          <RoomBookingCard key={room.id} room={room} favorite={favorites.has(room.id)} onToggleFavorite={toggleFavorite} imageVariant="favorite" />
        ))}
      </section>
    </PageShell>
  );
}

function AboutPage() {
  return (
    <PageShell>
      <PageHero image={pageAsset('about', 'hero.png')} title="Подлинное наследие Киклад в сердце Санторини" />
      <OrnamentDivider />
      <section className="page-intro">
        <div>
          <h1>Отель Aegean Lemon Heritage</h1>
          <p>Построен на месте старинных виноделен и пещерных домов острова. Мы сохранили первоначальную текстуру известняковых скал, добавив к ним премиальный комфорт.</p>
          <p>Наша цель — подарить гостям тишину, которую нарушает только морской бриз.</p>
        </div>
        <img src={pageAsset('about', 'dome.png')} alt="Синий купол на фоне моря" />
      </section>
      <section className="architecture-section">
        <img src={pageAsset('about', 'architecture.png')} alt="Архитектурная схема каскадов" />
        <div>
          <h2>Архитектура каскадов: наши ярусы</h2>
          <p><strong>Верхний ярус:</strong> панорамные люксы и приватные бассейны с лазурным горизонтом Кальдеры.</p>
          <p><strong>Средний ярус:</strong> аутентичные пещеры, бережно вписанные в склон ради тишины и прохлады.</p>
          <p><strong>Нижний ярус:</strong> сад и террасы, где лимонные деревья утопают в мягком южном свете.</p>
        </div>
      </section>
      <OrnamentDivider />
      <FeatureStack />
    </PageShell>
  );
}

function FeatureStack() {
  const features = [
    {
      title: 'Лимонный сад с зоной отдыха',
      image: pageAsset('about', 'lemon-garden.png'),
      text: 'Тенистый лимонный сад — тихое сердце отеля, где можно укрыться от полуденного средиземноморского зноя.',
      to: '/lemon-gardens',
    },
    {
      title: 'Инфинити-бассейн',
      image: pageAsset('about', 'pool.png'),
      text: 'Главный бассейн расположен на открытой террасе и плавно сливается с лазурным горизонтом Эгейского моря.',
    },
    {
      title: 'Ресторан гастрономического искусства',
      image: pageAsset('about', 'restaurant.png'),
      text: 'Наш ресторан предлагает изысканное кулинарное путешествие с захватывающим видом на бескрайнюю синеву.',
      to: '/gastronomy',
    },
  ];

  return (
    <section className="feature-stack">
      {features.map((feature) => (
        <article className="feature-row" key={feature.title}>
          <img src={feature.image} alt={feature.title} />
          <div>
            <h2>{feature.title}</h2>
            <p>{feature.text}</p>
            {feature.to && <Link className="text-link" to={feature.to}>Подробнее</Link>}
          </div>
        </article>
      ))}
    </section>
  );
}

function GastronomyPage() {
  return (
    <PageShell>
      <PageHero image={pageAsset('gastronomy', 'hero.png')} title="Вкус Средиземноморья на вершине Кальдеры" />
      <OrnamentDivider />
      <section className="page-intro page-intro--gastronomy">
        <div>
          <h1>Философия вкуса</h1>
          <p>Мы готовим только из того, что выросло под солнцем Санторини и было поймано в Эгейском море этим утром.</p>
          <p>Вулканическая почва острова придает овощам и травам уникальный, концентрированный вкус.</p>
        </div>
        <img src={pageAsset('gastronomy', 'philosophy.png')} alt="Сервировка завтрака у моря" />
      </section>
      <FoodRhythm page="gastronomy" />
      <section className="wide-cta">
        <img src={pageAsset('gastronomy', 'cta.png')} alt="Ужин на террасе" />
        <div>
          <h2>Разделите этот момент</h2>
          <Button>Забронировать стол</Button>
        </div>
      </section>
    </PageShell>
  );
}

function FoodRhythm({ page }) {
  const meals = [
    { title: 'Утро (08:00 — 12:00):', image: pageAsset(page, page === 'lemon-gardens' ? 'taste-1.png' : 'morning.png'), text: 'Воздушные греческие йогурты с медом из дикого тимьяна, свежая выпечка и лимонад.' },
    { title: 'День (13:00 — 17:00):', image: pageAsset(page, page === 'lemon-gardens' ? 'taste-2.png' : 'day.png'), text: 'Морепродукты на гриле, свежайшие салаты и фирменный лимонный сорбет.' },
    { title: 'Вечер (19:00 — 23:00):', image: pageAsset(page, page === 'lemon-gardens' ? 'taste-3.png' : 'evening.png'), text: 'Ужин под звездами, авторские блюда от шеф-повара и огни ночного острова.' },
  ];

  return (
    <section className="food-rhythm">
      {meals.map((meal) => (
        <article key={meal.title}>
          <img src={meal.image} alt={meal.title} />
          <p><strong>{meal.title}</strong> {meal.text}</p>
        </article>
      ))}
    </section>
  );
}

function LemonGardensPage() {
  return (
    <PageShell>
      <PageHero image={pageAsset('lemon-gardens', 'hero.png')} title="Лимонные сады, где начинается утро" />
      <OrnamentDivider />
      <section className="page-intro page-intro--gastronomy">
        <div>
          <h1>Сад как тихое сердце отеля</h1>
          <p>Здесь гости собирают свежие лимоны для утреннего напитка, гуляют по каменным дорожкам и отдыхают под мягкой тенью деревьев.</p>
          <p>Каждый плод становится частью кухни Lemon Heritage: от сорбета до соусов к морепродуктам.</p>
        </div>
        <img src={pageAsset('lemon-gardens', 'intro.png')} alt="Лимонный сад" />
      </section>
      <FoodRhythm page="lemon-gardens" />
      <section className="wide-cta">
        <img src={pageAsset('lemon-gardens', 'cta.png')} alt="Санторинский ужин с лимонами" />
        <div>
          <h2>Попробуйте вкус сада</h2>
          <Link className="button" to="/gastronomy">К гастрономии</Link>
        </div>
      </section>
    </PageShell>
  );
}

function RoomDetailsPage({ isAuthenticated }) {
  const { roomId } = useParams();
  const navigate = useNavigate();
  const room = getRoomById(roomId);
  const relatedRooms = rooms.filter((item) => item.id !== room.id).slice(0, 3);
  const gallery = [room.cardImage, room.showcaseImage, room.favoriteImage || room.image].filter(Boolean);
  const [activeImage, setActiveImage] = useState(0);
  const [dates, setDates] = useState({ checkIn: '2027-01-01', checkOut: '2027-01-04' });
  const nights = getNights(dates.checkIn, dates.checkOut);
  const total = nights * room.priceValue;
  const goImage = (direction) => setActiveImage((current) => (current + direction + gallery.length) % gallery.length);
  const updateDate = (key) => (event) => setDates((current) => ({ ...current, [key]: event.target.value }));

  function reserve() {
    const bookingPath = `/booking/${room.id}?checkIn=${dates.checkIn}&checkOut=${dates.checkOut}`;
    if (isAuthenticated) {
      navigate(bookingPath);
      return;
    }
    navigate(`/login?returnTo=${encodeURIComponent(bookingPath)}`);
  }

  return (
    <PageShell>
      <section className="room-detail">
        <div className="room-detail__gallery">
          <img src={gallery[activeImage]} alt={room.title} />
          <button type="button" className="room-detail__arrow room-detail__arrow--left" onClick={() => goImage(-1)} aria-label="Предыдущее фото">
            <ChevronLeft size={34} />
          </button>
          <button type="button" className="room-detail__arrow room-detail__arrow--right" onClick={() => goImage(1)} aria-label="Следующее фото">
            <ChevronRight size={34} />
          </button>
          <div className="room-detail__thumbs">
            {gallery.map((image, index) => (
              <button key={`${image}-${index}`} type="button" className={index === activeImage ? 'is-active' : ''} onClick={() => setActiveImage(index)}>
                <img src={image} alt="" />
              </button>
            ))}
          </div>
        </div>
        <div className="room-detail__info">
          <p className="eyebrow">Просмотр номера</p>
          <h1>{room.title}</h1>
          <p className="room-meta-line">{room.meta}</p>
          <p>{room.showcaseDescription}</p>
          <ul>
            <li>Ежедневный завтрак с лимонным сорбетом</li>
            <li>Панорамная зона отдыха и натуральный лен</li>
            <li>Wi-Fi, кондиционер, халаты и уходовая косметика</li>
          </ul>
          <strong>{room.price} / ночь</strong>
        </div>
      </section>
      <section className="stay-panel">
        <div>
          <h2>Выберите даты</h2>
          <p>Стоимость пересчитывается по количеству ночей.</p>
        </div>
        <label>
          Заезд
          <input type="date" value={dates.checkIn} onChange={updateDate('checkIn')} onInput={updateDate('checkIn')} />
        </label>
        <label>
          Выезд
          <input type="date" value={dates.checkOut} onChange={updateDate('checkOut')} onInput={updateDate('checkOut')} />
        </label>
        <div className="stay-panel__total">
          <span>{nights} ночи</span>
          <strong>{formatMoney(total)}</strong>
        </div>
        <Button type="button" onClick={reserve}>Забронировать</Button>
      </section>
      <section className="related-rooms">
        <h2>Похожие номера</h2>
        <div className="related-rooms__grid">
          {relatedRooms.map((item) => (
            <RoomBookingCard key={item.id} room={item} favorite={false} />
          ))}
        </div>
      </section>
    </PageShell>
  );
}

function BookingPage() {
  const { roomId } = useParams();
  const location = useLocation();
  const room = getRoomById(roomId);
  const query = new URLSearchParams(location.search);
  const [dates, setDates] = useState({
    checkIn: query.get('checkIn') || '2027-01-01',
    checkOut: query.get('checkOut') || '2027-01-04',
  });
  const [form, setForm] = useState({ lastName: '', firstName: '', middleName: '', phone: '', payment: 'Карта' });
  const [submitted, setSubmitted] = useState(false);
  const nights = getNights(dates.checkIn, dates.checkOut);
  const total = nights * room.priceValue;
  const updateForm = (key) => (event) => setForm((current) => ({ ...current, [key]: event.target.value }));
  const updateBookingDate = (key) => (event) => setDates((current) => ({ ...current, [key]: event.target.value }));

  return (
    <PageShell>
      <section className="booking-page">
        <div className="booking-summary">
          <img src={room.cardImage} alt={room.title} />
          <h1>{room.title}</h1>
          <p>{room.meta}</p>
          <strong>{formatMoney(total)}</strong>
          <span>{nights} ночи · {room.price} / ночь</span>
        </div>
        <form className="booking-form" onSubmit={(event) => {
          event.preventDefault();
          setSubmitted(true);
        }}>
          <h2>Данные гостя</h2>
          <label>Фамилия<input required value={form.lastName} onChange={updateForm('lastName')} /></label>
          <label>Имя<input required value={form.firstName} onChange={updateForm('firstName')} /></label>
          <label>Отчество<input required value={form.middleName} onChange={updateForm('middleName')} /></label>
          <label>Номер телефона<input required type="tel" placeholder="+7 999 000-00-00" value={form.phone} onChange={updateForm('phone')} /></label>
          <div className="booking-form__dates">
            <label>Заезд<input type="date" value={dates.checkIn} onChange={updateBookingDate('checkIn')} onInput={updateBookingDate('checkIn')} /></label>
            <label>Выезд<input type="date" value={dates.checkOut} onChange={updateBookingDate('checkOut')} onInput={updateBookingDate('checkOut')} /></label>
          </div>
          <fieldset>
            <legend>Способ оплаты</legend>
            {['Карта', 'Наличные при заезде', 'Перевод'].map((payment) => (
              <label key={payment}>
                <input type="radio" name="payment" value={payment} checked={form.payment === payment} onChange={updateForm('payment')} />
                {payment}
              </label>
            ))}
          </fieldset>
          {submitted && <p className="booking-success">Заявка подготовлена. Это фронтовая демонстрация без отправки в БД.</p>}
          <Button type="submit">Подтвердить бронь</Button>
        </form>
      </section>
    </PageShell>
  );
}

function Field({ label, type = 'text', value, onChange, required = true, autoComplete }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input type={type} value={value} onChange={(event) => onChange(event.target.value)} required={required} autoComplete={autoComplete} />
    </label>
  );
}

function AuthLayout({ children }) {
  return (
    <>
      <Header />
      <main className="auth-page">
        <div className="auth-background" aria-hidden="true" />
        {children}
      </main>
      <Footer />
    </>
  );
}

function LoginPage({ onAuthenticated }) {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  function submit(event) {
    event.preventDefault();
    if (!email.includes('@') || password.length < 4) {
      setError('Введите корректную почту и пароль не короче 4 символов.');
      return;
    }
    onAuthenticated();
    const returnTo = new URLSearchParams(window.location.search).get('returnTo');
    navigate(returnTo || '/account');
  }

  return (
    <AuthLayout>
      <form className="auth-card" onSubmit={submit}>
        <CircleUserRound className="auth-icon" size={86} />
        <h1>Вход в аккаунт</h1>
        <Field label="Введите почту" type="email" value={email} onChange={setEmail} autoComplete="email" />
        <Field label="Введите пароль" type="password" value={password} onChange={setPassword} autoComplete="current-password" />
        {error && <p className="form-error">{error}</p>}
        <Button type="submit">Войти</Button>
        <Link className="auth-link" to="/register">Зарегистрироваться, если нет аккаунта</Link>
      </form>
    </AuthLayout>
  );
}

function RegisterPage() {
  const navigate = useNavigate();
  const [step, setStep] = useState(1);
  const [form, setForm] = useState({ lastName: '', firstName: '', middleName: '', email: '', password: '', repeatPassword: '' });
  const [error, setError] = useState('');
  const update = (key) => (value) => {
    setError('');
    setForm((current) => ({ ...current, [key]: value }));
  };

  function submit(event) {
    event.preventDefault();
    if (step === 1) {
      if (!form.lastName || !form.firstName || !form.middleName) {
        setError('Заполните фамилию, имя и отчество.');
        return;
      }
      setStep(2);
      return;
    }
    if (!form.email.includes('@') || form.password.length < 6 || form.password !== form.repeatPassword) {
      setError('Проверьте почту и совпадение паролей. Минимум 6 символов.');
      return;
    }
    navigate('/account');
  }

  return (
    <AuthLayout>
      <form className="auth-card" onSubmit={submit}>
        <CircleUserRound className="auth-icon" size={86} />
        <h1>Регистрация</h1>
        <div className="stepper" aria-label={`Шаг ${step} из 2`}>
          <span className="is-active" />
          <span className={step === 2 ? 'is-active' : ''} />
        </div>
        {step === 1 ? (
          <>
            <Field label="Фамилия" value={form.lastName} onChange={update('lastName')} autoComplete="family-name" />
            <Field label="Имя" value={form.firstName} onChange={update('firstName')} autoComplete="given-name" />
            <Field label="Отчество" value={form.middleName} onChange={update('middleName')} autoComplete="additional-name" />
          </>
        ) : (
          <>
            <Field label="Почта" type="email" value={form.email} onChange={update('email')} autoComplete="email" />
            <Field label="Пароль" type="password" value={form.password} onChange={update('password')} autoComplete="new-password" />
            <Field label="Повторный пароль" type="password" value={form.repeatPassword} onChange={update('repeatPassword')} autoComplete="new-password" />
          </>
        )}
        {error && <p className="form-error">{error}</p>}
        <Button type="submit">{step === 1 ? 'Далее' : 'Зарегистрироваться'}</Button>
        {step === 2 && <button type="button" className="text-button" onClick={() => setStep(1)}>Вернуться к данным</button>}
      </form>
    </AuthLayout>
  );
}

function AccountPage() {
  const roomById = useMemo(() => new Map(rooms.map((room) => [room.id, room])), []);

  return (
    <>
      <Header />
      <main className="account-page">
        <div className="auth-background" aria-hidden="true" />
        <section className="account-card">
          <div className="account-profile">
            <div className="account-name">
              <CircleUserRound size={96} />
              <h1>Иванов Иван Иванович</h1>
            </div>
            <nav className="account-actions" aria-label="Действия аккаунта">
              <button>Изменить данные</button>
              <button>Мои отзывы</button>
              <button>Избранные номера</button>
              <button><Trash2 size={20} />Удалить аккаунт</button>
              <button className="is-filled"><LogOut size={20} />Выйти из аккаунта</button>
            </nav>
          </div>
          <div className="booking-history">
            <h2>История броней:</h2>
            {bookingHistory.map((booking) => (
              <article className="booking-card" key={booking.id}>
                <img src={booking.image} alt={booking.title} />
                <div>
                  <h3>{booking.title}</h3>
                  <p>{roomById.get(booking.roomId)?.meta}</p>
                  <p>{booking.dates}</p>
                  <p>{booking.status}</p>
                  <p>{booking.price}</p>
                  <p>{booking.note}</p>
                  <div className="booking-card__actions">
                    <Button type="button" variant="outline">Отменить бронь</Button>
                    <Button type="button" variant="outline"><Phone size={18} />Позвонить</Button>
                  </div>
                </div>
              </article>
            ))}
          </div>
        </section>
      </main>
      <Footer />
    </>
  );
}

export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/rooms" element={<RoomsPage />} />
        <Route path="/rooms/:roomId" element={<RoomDetailsPage isAuthenticated={isAuthenticated} />} />
        <Route path="/booking/:roomId" element={<BookingPage />} />
        <Route path="/favorites" element={<FavoritesPage />} />
        <Route path="/about" element={<AboutPage />} />
        <Route path="/gastronomy" element={<GastronomyPage />} />
        <Route path="/lemon-gardens" element={<LemonGardensPage />} />
        <Route path="/login" element={<LoginPage onAuthenticated={() => setIsAuthenticated(true)} />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/account" element={<AccountPage />} />
      </Routes>
    </BrowserRouter>
  );
}
