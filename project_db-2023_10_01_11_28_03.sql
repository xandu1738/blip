--
-- PostgreSQL database dump
--

-- Dumped from database version 14.8 (Homebrew)
-- Dumped by pg_dump version 14.8 (Homebrew)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: system_domain; Type: TABLE; Schema: public; Owner: jet
--

CREATE TABLE public.system_domain (
    id bigint NOT NULL,
    domain_name character varying
);


ALTER TABLE public.system_domain OWNER TO jet;

--
-- Name: system_domain_id_seq; Type: SEQUENCE; Schema: public; Owner: jet
--

CREATE SEQUENCE public.system_domain_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.system_domain_id_seq OWNER TO jet;

--
-- Name: system_domain_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jet
--

ALTER SEQUENCE public.system_domain_id_seq OWNED BY public.system_domain.id;


--
-- Name: system_permission; Type: TABLE; Schema: public; Owner: jet
--

CREATE TABLE public.system_permission (
    id bigint NOT NULL,
    permission_code character varying NOT NULL,
    permission_name character varying,
    domain character varying
);


ALTER TABLE public.system_permission OWNER TO jet;

--
-- Name: system_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: jet
--

CREATE SEQUENCE public.system_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.system_permission_id_seq OWNER TO jet;

--
-- Name: system_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jet
--

ALTER SEQUENCE public.system_permission_id_seq OWNED BY public.system_permission.id;


--
-- Name: system_role; Type: TABLE; Schema: public; Owner: jet
--

CREATE TABLE public.system_role (
    id bigint NOT NULL,
    role_name character varying NOT NULL,
    role_code character varying NOT NULL,
    created_at timestamp without time zone DEFAULT now(),
    domain character varying
);


ALTER TABLE public.system_role OWNER TO jet;

--
-- Name: system_role_id_seq; Type: SEQUENCE; Schema: public; Owner: jet
--

CREATE SEQUENCE public.system_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.system_role_id_seq OWNER TO jet;

--
-- Name: system_role_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jet
--

ALTER SEQUENCE public.system_role_id_seq OWNED BY public.system_role.id;


--
-- Name: system_role_permission_assignment; Type: TABLE; Schema: public; Owner: jet
--

CREATE TABLE public.system_role_permission_assignment (
    id bigint NOT NULL,
    permission_code character varying NOT NULL,
    role_code character varying NOT NULL
);


ALTER TABLE public.system_role_permission_assignment OWNER TO jet;

--
-- Name: system_role_permission_assignment_id_seq; Type: SEQUENCE; Schema: public; Owner: jet
--

CREATE SEQUENCE public.system_role_permission_assignment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.system_role_permission_assignment_id_seq OWNER TO jet;

--
-- Name: system_role_permission_assignment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jet
--

ALTER SEQUENCE public.system_role_permission_assignment_id_seq OWNED BY public.system_role_permission_assignment.id;


--
-- Name: system_user; Type: TABLE; Schema: public; Owner: jet
--

CREATE TABLE public.system_user (
    id bigint NOT NULL,
    first_name character varying,
    last_name character varying,
    password character varying NOT NULL,
    email character varying NOT NULL,
    username character varying,
    role_code character varying,
    created_at timestamp without time zone DEFAULT now(),
    last_logged_in_at timestamp without time zone,
    is_active boolean DEFAULT false
);


ALTER TABLE public.system_user OWNER TO jet;

--
-- Name: system_user_id_seq; Type: SEQUENCE; Schema: public; Owner: jet
--

CREATE SEQUENCE public.system_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.system_user_id_seq OWNER TO jet;

--
-- Name: system_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jet
--

ALTER SEQUENCE public.system_user_id_seq OWNED BY public.system_user.id;


--
-- Name: system_domain id; Type: DEFAULT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_domain ALTER COLUMN id SET DEFAULT nextval('public.system_domain_id_seq'::regclass);


--
-- Name: system_permission id; Type: DEFAULT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_permission ALTER COLUMN id SET DEFAULT nextval('public.system_permission_id_seq'::regclass);


--
-- Name: system_role id; Type: DEFAULT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_role ALTER COLUMN id SET DEFAULT nextval('public.system_role_id_seq'::regclass);


--
-- Name: system_role_permission_assignment id; Type: DEFAULT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_role_permission_assignment ALTER COLUMN id SET DEFAULT nextval('public.system_role_permission_assignment_id_seq'::regclass);


--
-- Name: system_user id; Type: DEFAULT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_user ALTER COLUMN id SET DEFAULT nextval('public.system_user_id_seq'::regclass);


--
-- Name: system_domain_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jet
--

SELECT pg_catalog.setval('public.system_domain_id_seq', 10, true);


--
-- Name: system_permission_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jet
--

SELECT pg_catalog.setval('public.system_permission_id_seq', 10, true);


--
-- Name: system_role_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jet
--

SELECT pg_catalog.setval('public.system_role_id_seq', 3, true);


--
-- Name: system_role_permission_assignment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jet
--

SELECT pg_catalog.setval('public.system_role_permission_assignment_id_seq', 10, true);


--
-- Name: system_user_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jet
--

SELECT pg_catalog.setval('public.system_user_id_seq', 1, false);


--
-- Name: system_domain system_domain_domain_name_key; Type: CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_domain
    ADD CONSTRAINT system_domain_domain_name_key UNIQUE (domain_name);


--
-- Name: system_domain system_domain_pkey; Type: CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_domain
    ADD CONSTRAINT system_domain_pkey PRIMARY KEY (id);


--
-- Name: system_permission system_permission_permission_code_key; Type: CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_permission
    ADD CONSTRAINT system_permission_permission_code_key UNIQUE (permission_code);


--
-- Name: system_permission system_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_permission
    ADD CONSTRAINT system_permission_pkey PRIMARY KEY (id);


--
-- Name: system_role_permission_assignment system_role_permission_assignment_pkey; Type: CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_role_permission_assignment
    ADD CONSTRAINT system_role_permission_assignment_pkey PRIMARY KEY (id);


--
-- Name: system_role system_role_pkey; Type: CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_role
    ADD CONSTRAINT system_role_pkey PRIMARY KEY (id);


--
-- Name: system_role system_role_role_code_key; Type: CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_role
    ADD CONSTRAINT system_role_role_code_key UNIQUE (role_code);


--
-- Name: system_user system_user_email_key; Type: CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_user
    ADD CONSTRAINT system_user_email_key UNIQUE (email);


--
-- Name: system_user system_user_pkey; Type: CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_user
    ADD CONSTRAINT system_user_pkey PRIMARY KEY (id);


--
-- Name: system_user system_user_username_key; Type: CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_user
    ADD CONSTRAINT system_user_username_key UNIQUE (username);


--
-- Name: system_role_permission_assignment system_role_permission_assignment_system_permission_code_fk; Type: FK CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_role_permission_assignment
    ADD CONSTRAINT system_role_permission_assignment_system_permission_code_fk FOREIGN KEY (permission_code) REFERENCES public.system_permission(permission_code) ON DELETE CASCADE;


--
-- Name: system_role_permission_assignment system_role_permission_assignment_system_role_role_code_fk; Type: FK CONSTRAINT; Schema: public; Owner: jet
--

ALTER TABLE ONLY public.system_role_permission_assignment
    ADD CONSTRAINT system_role_permission_assignment_system_role_role_code_fk FOREIGN KEY (role_code) REFERENCES public.system_role(role_code) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

